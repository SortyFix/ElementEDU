package de.gaz.eedu.blogging;

import de.gaz.eedu.entity.model.EntityModel;
import org.jetbrains.annotations.NotNull;

public record PostModel(@NotNull Long id, @NotNull Long authorId, @NotNull String title,
                        @NotNull String body, @NotNull Long timeOfCreation, @NotNull String[] privileges,
                        @NotNull String[] tags) implements EntityModel
{

    @Override
    public @NotNull Long id()
    {
        return null;
    }
}
