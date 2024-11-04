package de.gaz.eedu.blogging;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.file.FileCreateModel;
import de.gaz.eedu.file.FileEntity;
import de.gaz.eedu.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Getter;
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
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class PostService extends EntityService<PostRepository, PostEntity, PostModel, PostCreateModel>
{
    @Getter private final PostRepository repository;
    @Getter private final UserService userService;

    @Value("${blog.write}") private String writePrivilege;

    @Transactional
    public @NotNull List<PostEntity> createEntity(@NotNull Set<PostCreateModel> model) throws CreationException
    {
        return saveEntity(model.stream().map(postCreateModel -> postCreateModel.toEntity(new PostEntity())).toList());
    }

    /**
     * Deletes a post from the database.
     *
     * @throws de.gaz.eedu.exception.EntityUnknownException If post or user could not be found in the database.
     * @throws org.springframework.web.client.HttpClientErrorException.Unauthorized If the user does not own the privileges to edit a post.
     */
    public void deleteEntity(@NotNull Long postId)
    {
        getRepository().delete(getRepository().getReferenceById(postId));
    }

    public boolean userHasReadAuthority(@NotNull Long userId, @NotNull Long postId)
    {
        return getUserService().loadEntityByIDSafe(userId).hasAnyAuthority(getRepository().getReferenceById(postId).getReadPrivileges());
    }

    public boolean userHasEditAuthority(@NotNull Long userId, @NotNull Long postId)
    {
        return getUserService().loadEntityByIDSafe(userId).hasAnyAuthority(getRepository().getReferenceById(postId).getEditPrivileges());
    }

    public @NotNull PostModel getModel(@NotNull Long postId)
    {
        return getRepository().getReferenceById(postId).toModel();
    }

    /**
     * Edits an existing model by setting and saving all given values.
     * @param postId the id of the post to edit
     * @param author the name of the author; Has no connection to other UserEntities, merely for style
     * @param title the title of the post
     * @param body the body of the post
     * @return PostModel containing the given values
     */
    public @NotNull PostModel editModel(@NotNull Long postId, @NotNull String author, @NotNull String title, @NotNull String body)
    {
        PostEntity postEntity = getRepository().getReferenceById(postId);
        postEntity.setAuthor(author);
        postEntity.setTitle(title);
        postEntity.setBody(body);
        getRepository().save(postEntity);
        return postEntity.toModel();
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
        PostEntity postEntity = getRepository().getReferenceById(postId);
        FileUtils.deleteDirectory(new File(postEntity.getThumbnailURL()));
        FileEntity thumbnailFile = new FileCreateModel(userId, newThumbnail.getName(), postEntity.getReadPrivileges().toArray(new String[0]), "blog", postEntity.getTags().toArray(String[]::new)).toEntity(new FileEntity());
        thumbnailFile.uploadBatch("", newThumbnail);
        postEntity.setThumbnailURL(thumbnailFile.getFilePath());
        getRepository().save(postEntity);
        return postEntity.toModel();
    }

    /**
     * Service method to create a new blog post.
     *
     * @param userId the ID of the user creating the post
     * @param thumbnail the uploaded MultipartFile for the post's thumbnail
     * @param createModel the template of the post about to be created
     * @return PostModel containing given data
     */
    @Transactional
    public @NotNull PostModel createPost(@NotNull Long userId, @NotNull MultipartFile thumbnail, @NotNull PostCreateModel createModel)
    {
        if(getUserService().loadEntityByIDSafe(userId).hasAuthority(writePrivilege))
        {
            FileEntity thumbnailFile = new FileCreateModel(userId, thumbnail.getName(), createModel.readPrivileges(), "blog", createModel.tags()).toEntity(new FileEntity());
            thumbnailFile.uploadBatch("", thumbnail);
            return createEntity(new PostCreateModel(createModel.author(), createModel.title(),
                    thumbnailFile.getFilePath(), createModel.body(), createModel.readPrivileges(), createModel.editPrivileges(), createModel.tags())).toModel();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}
