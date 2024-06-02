package de.gaz.eedu.blogging;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.file.FileCreateModel;
import de.gaz.eedu.file.FileEntity;
import de.gaz.eedu.file.FileService;
import de.gaz.eedu.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class PostService extends EntityService<PostRepository, PostEntity, PostModel, PostCreateModel>
{
    private PostRepository postRepository;
    private UserService userService;
    private FileService fileService;

    @Value("${blog.write}") private String writePrivilege;

    @Override
    public @NotNull PostRepository getRepository()
    {
        return postRepository;
    }

    @Override
    public @NotNull PostEntity createEntity(@NotNull PostCreateModel model) throws CreationException
    {
        return model.toEntity(new PostEntity());
    }

    /**
     * Deletes a post from the database.
     * <p>
     *     Requires the user loaded by the given <code>userId</code>
     *     to maintain edit authority over the requested post.
     * </p>
     * @param userId
     * @param postId
     * @throws de.gaz.eedu.exception.EntityUnknownException If post or user could not be found in the database.
     * @throws org.springframework.web.client.HttpClientErrorException.Unauthorized If the user does not own the privileges to edit a post.
     */
    public void deleteEntity(@NotNull Long userId, @NotNull Long postId)
    {
        if(userHasEditAuthority(userId, postId))
        {
            postRepository.delete(postRepository.getReferenceById(postId));
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    public boolean userHasReadAuthority(@NotNull Long userId, @NotNull Long postId)
    {
        return userService.loadEntityByIDSafe(userId).hasAnyAuthority(postRepository.getReferenceById(postId).getReadPrivileges());
    }

    public boolean userHasEditAuthority(@NotNull Long userId, @NotNull Long postId)
    {
        return userService.loadEntityByIDSafe(userId).hasAnyAuthority(postRepository.getReferenceById(postId).getEditPrivileges());
    }

    public @NotNull PostModel getModel(@NotNull Long userId, @NotNull Long postId)
    {
        if(userHasReadAuthority(userId, postId))
        {
            return postRepository.getReferenceById(postId).toModel();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found: " + postId);
    }

    /**
     * Edits an existing model by setting and saving all given values.
     * @param userId the id of the user editing the post; throws <code>UNAUTHORIZED</code> if the user does not have edit privileges.
     * @param postId the id of the post to edit
     * @param author the name of the author; Has no connection to other UserEntities, merely for style
     * @param title the title of the post
     * @param body the body of the post
     * @param readPrivileges only people with read privileges will be able to read the created post
     * @param editPrivileges only people with edit privileges will be able to edit the created post
     * @param tags tags of the post to filter posts with certain tags
     * @return PostModel containing the given values
     */
    public @NotNull PostModel editModel(@NotNull Long userId, @NotNull Long postId, @NotNull String author, @NotNull String title, @NotNull String body,
            @NotNull String[] readPrivileges, @NotNull String[] editPrivileges, @NotNull String[] tags)
    {
        if(userHasEditAuthority(userId, postId))
        {
            PostEntity postEntity = getRepository().getReferenceById(postId);
            postEntity.setAuthor(author);
            postEntity.setTitle(title);
            postEntity.setBody(body);
            postEntity.setReadPrivileges(new HashSet<>(Arrays.asList(readPrivileges)));
            postEntity.setEditPrivileges(new HashSet<>(Arrays.asList(editPrivileges)));
            postEntity.setTags(new HashSet<>(Arrays.asList(tags)));
            postRepository.save(postEntity);
            return postEntity.toModel();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Replaces the thumbnail of a post with another image.
     *
     * @param userId ID of the user attempting to edit the thumbnail
     * @param postId ID of the post the user is trying to edit
     * @param newThumbnail MultipartFile pf the new thumbnail uploaded by the user
     * @return PostModel containing the new data
     *
     * @throws IOException if the image could not be found on the file system
     */
    public @NotNull PostModel editThumbnail(@NotNull Long userId, @NotNull Long postId, @NotNull MultipartFile newThumbnail) throws IOException
    {
        if(userHasEditAuthority(userId, postId))
        {
            PostEntity postEntity = getRepository().getReferenceById(postId);
            FileUtils.deleteDirectory(new File(postEntity.getThumbnailURL()));
            FileEntity thumbnailFile = new FileCreateModel(userId, newThumbnail.getName(), postEntity.getReadPrivileges().toArray(new String[0]), "blog", postEntity.getTags().toArray(String[]::new)).toEntity(new FileEntity());
            thumbnailFile.uploadBatch("", newThumbnail);
            postEntity.setThumbnailURL(thumbnailFile.getFilePath());
            postRepository.save(postEntity);
            return postEntity.toModel();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Service method to create a new blog post.
     *
     * @param userId the ID of the user creating the post
     * @param author the name of the author; Has no connection to other UserEntities, merely for style
     * @param title the title of the post
     * @param thumbnail the uploaded MultipartFile for the post's thumbnail
     * @param body the body of the post
     * @param readPrivileges only people with read privileges will be able to read the created post
     * @param editPrivileges only people with edit privileges will be able to edit the created post
     * @param tags tags of the post to filter posts with certain tags
     * @return PostModel containing given data
     */
    @Transactional
    public @NotNull PostModel createPost(@NotNull Long userId, @NotNull String author, @NotNull String title, @NotNull MultipartFile thumbnail, @NotNull String body,
            @NotNull String[] readPrivileges, @NotNull String[] editPrivileges, @NotNull String[] tags)
    {
        if(userService.loadEntityByIDSafe(userId).hasAuthority(writePrivilege))
        {
            FileEntity thumbnailFile = new FileCreateModel(userId, thumbnail.getName(), readPrivileges, "blog", tags).toEntity(new FileEntity());
            thumbnailFile.uploadBatch("", thumbnail);
            return createEntity(new PostCreateModel(author, title,
                    thumbnailFile.getFilePath(), body, readPrivileges, editPrivileges, tags)).toModel();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}
