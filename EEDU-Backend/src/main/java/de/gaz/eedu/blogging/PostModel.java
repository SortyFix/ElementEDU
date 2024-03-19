package de.gaz.eedu.blogging;

import de.gaz.eedu.entity.model.EntityModel;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public record PostModel(@NotNull Long id, @NotNull Long authorId, @NotNull String title,
                        @NotNull String body, @NotNull Long timeOfCreation, @NotNull String[] privileges,
                        @NotNull String[] tags) implements EntityModel
{
    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostModel postModel = (PostModel) o;
        return Objects.equals(id, postModel.id) && Objects.equals(authorId, postModel.authorId) && Objects.equals(title,
                postModel.title) && Objects.equals(body, postModel.body) && Objects.equals(timeOfCreation,
                postModel.timeOfCreation) && Arrays.equals(privileges, postModel.privileges) && Arrays.equals(tags,
                postModel.tags);
    }

    @Override
    public int hashCode()
    {
        int result = Objects.hash(id, authorId, title, body, timeOfCreation);
        result = 31 * result + Arrays.hashCode(privileges);
        result = 31 * result + Arrays.hashCode(tags);
        return result;
    }
}
