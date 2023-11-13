package de.gaz.eedu.user.file;

import de.gaz.eedu.entity.model.CreationModel;
import jakarta.validation.constraints.NotEmpty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public record FileCreateModel(@NotNull String name,
                              @NotNull Long authorId,
                              @NotNull String filePath,
                              @NotEmpty Set<Long> permittedUsers,
                              @NotEmpty Set<Long> permittedGroups,
                              Set<String> tags) implements CreationModel<FileEntity>
{
    @Contract(pure = true) @Override public @NotNull String toString()
    {
        return "FileCreateModel{" +
                "fileName='" + name + '\'' +
                ", filePath='" + filePath + '\'' +
                ", permittedUsers=" + permittedUsers +
                ", permittedGroups=" + permittedGroups +
                ", tags='" + tags + '\'' +
                '}';
    }

    public @NotNull FileEntity toEntity(@NotNull FileEntity fileEntity) {
        FileEntity.builder()
                .fileName(name)
                .authorId(authorId)
                .filePath(filePath)
                .permittedUsers(new HashSet<>()) // TODO
                .permittedGroups(new HashSet<>()) // TODO
                .tags(tags);
        return fileEntity;
    }
}
