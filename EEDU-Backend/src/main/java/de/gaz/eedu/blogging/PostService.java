package de.gaz.eedu.blogging;

import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.CreationException;
import de.gaz.eedu.user.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

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
        PostEntity entity = model.toEntity(new PostEntity());
        entity.setAuthor(userService.loadEntityByIDSafe(model.authorId()));
        return entity;
    }
}
