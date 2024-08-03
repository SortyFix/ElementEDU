package de.gaz.eedu.blogging;

import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;

public record PostCreateModel(@NotNull String author, @NotNull String title, @NotNull String thumbnailURL, @NotNull String body,
                              @NotNull String[] readPrivileges, @NotNull String[] editPrivileges, @NotNull String[] tags) implements CreationModel<PostEntity>
{

    @Override
    public @NotNull String name()
    {
        return title() + System.currentTimeMillis();
    }

    @Override
    public @NotNull PostEntity toEntity(@NotNull PostEntity entity)
    {
        entity.setTitle(title());
        entity.setBody(body());
        entity.setAuthor(author());
        entity.setThumbnailURL(thumbnailURL());

        entity.attachReadPrivileges(readPrivileges());
        entity.attachReadPrivileges(editPrivileges());

        entity.attachEditPrivileges(editPrivileges());
        entity.setTimeOfCreation(System.currentTimeMillis());
        entity.attachTags(tags());
        return entity;
    }
}
