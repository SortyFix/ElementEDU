package de.gaz.eedu.blogging;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
public class PostService extends EntityService<PostRepository, PostEntity, PostModel, PostCreateModel>
{
    private PostRepository postRepository;
    private UserService userService;

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

    public @NotNull PostModel getModel(@NotNull Long userId, @NotNull Long postId)
    {
        if(userService.loadEntityByIDSafe(userId).hasAnyAuthority(postRepository.getReferenceById(postId).getPrivileges()))
        {
            return postRepository.getReferenceById(postId).toModel();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found: " + postId);
    }
}
