package de.gaz.eedu.blogging;

import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PostCreateModel(@NotNull String author, @NotNull String title, @Nullable String thumbnailURL, @NotNull String body,
                              @NotNull String[] editPrivileges, @NotNull String[] tags) implements CreationModel<Long, PostEntity>
{

    @Override
    public @NotNull PostEntity toEntity(@NotNull PostEntity entity)
    {
        entity.setTitle(title());
        entity.setBody(body());
        entity.setAuthor(author());
        entity.setThumbnailURL(thumbnailURL());
        entity.attachEditPrivileges(editPrivileges());
        entity.setTimeOfCreation(System.currentTimeMillis());
        entity.attachTags(tags());
        return entity;
    }
}
