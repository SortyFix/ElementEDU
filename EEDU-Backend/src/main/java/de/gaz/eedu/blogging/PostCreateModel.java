package de.gaz.eedu.blogging;

import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record PostCreateModel(@NotNull String author, @NotNull String title, @NotNull String thumbnailURL, @NotNull String body,
                              @NotNull String[] readPrivileges, @NotNull String[] editPrivileges, @NotNull String[] tags) implements CreationModel<PostEntity>
{

    @Override
    public @NotNull PostEntity toEntity(@NotNull PostEntity entity)
    {
        entity.setTitle(title());
        entity.setBody(body());
        entity.setAuthor(author());
        entity.setThumbnailURL(thumbnailURL());
        entity.attachReadPrivileges(combinePrivileges().toArray(new String[0]));
        entity.attachEditPrivileges(editPrivileges());
        entity.setTimeOfCreation(System.currentTimeMillis());
        entity.attachTags(tags());
        return entity;
    }

    public @NotNull @Unmodifiable Set<String> combinePrivileges()
    {
        return Stream.concat(Arrays.stream(readPrivileges()), Arrays.stream(editPrivileges())).collect(Collectors.toUnmodifiableSet());
    }
}
