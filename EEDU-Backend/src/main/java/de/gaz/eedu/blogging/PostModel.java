package de.gaz.eedu.blogging;

import de.gaz.eedu.entity.model.EntityModel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record PostModel(@NotNull Long id, @NotNull String author, @NotNull String title,
                        @NotNull String thumbnailBlob, @NotNull String body, @NotNull Long timeOfCreation, @NotNull String[] readPrivileges,
                        @NotNull String[] editPrivileges, @NotNull String[] tags) implements EntityModel
{
    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostModel postModel = (PostModel) o;
        return Objects.equals(id, postModel.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id());
    }
}
