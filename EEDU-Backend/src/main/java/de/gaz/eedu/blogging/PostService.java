package de.gaz.eedu.blogging;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.file.FileCreateModel;
import de.gaz.eedu.file.FileEntity;
import de.gaz.eedu.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.HashSet;

@RequiredArgsConstructor
public class PostService extends EntityService<PostRepository, PostEntity, PostModel, PostCreateModel>
{
    private PostRepository postRepository;
    private UserService userService;

    @Value("${blog.write}") private final String writePrivilege;

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

    public @NotNull PostModel editModel(@NotNull Long userId, @NotNull Long postId, @NotNull String author, @NotNull String title, @NotNull String thumbnailURL, @NotNull String body,
            @NotNull String[] readPrivileges, @NotNull String[] editPrivileges, @NotNull String[] tags)
    {
        if(userHasEditAuthority(userId, postId))
        {
            PostEntity postEntity = getRepository().getReferenceById(postId);
            postEntity.setAuthor(author);
            postEntity.setTitle(title);
            postEntity.setThumbnailURL(thumbnailURL);
            postEntity.setBody(body);
            postEntity.setReadPrivileges(new HashSet<>(Arrays.asList(readPrivileges)));
            postEntity.setEditPrivileges(new HashSet<>(Arrays.asList(editPrivileges)));
            postEntity.setTags(new HashSet<>(Arrays.asList(tags)));
            postRepository.save(postEntity);
            return postEntity.toModel();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

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
