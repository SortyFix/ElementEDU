package de.gaz.eedu.file;

import de.gaz.eedu.entity.model.CreationModel;
import jakarta.validation.constraints.NotEmpty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record FileCreateModel(@NotNull Long authorId,
                              @NotNull String filePath,
                              @NotEmpty Set<Long> permittedUsers,
                              @NotEmpty Set<Long> permittedGroups,
                              Set<String> tags) implements CreationModel<FileEntity>
{
    @Contract(pure = true) @Override public @NotNull String toString()
    {
        return "FileCreateModel{" +
                ", filePath='" + filePath + '\'' +
                ", permittedUsers=" + permittedUsers +
                ", permittedGroups=" + permittedGroups +
                ", tags='" + tags + '\'' +
                '}';
    }

    @Override
    public @NotNull String name()
    {
        return filePath + " " + System.currentTimeMillis();
    }

    public @NotNull FileEntity toEntity(@NotNull FileEntity fileEntity) {
        FileEntity.builder()
                .authorId(authorId)
                .filePath(filePath)
                .permittedUsers(permittedUsers) // TODO | 11.12.2023: what?
                .permittedGroups(permittedGroups) // TODO | same here
                .tags(tags);
        return fileEntity;
    }
}
