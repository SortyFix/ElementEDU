package de.gaz.eedu.file;

import de.gaz.eedu.entity.model.CreationModel;
import jakarta.validation.constraints.NotEmpty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public record FileCreateModel(@NotNull Long authorId,
                              @NotNull String filePath,
                              @NotEmpty String[] privilege,
                              String[] tags) implements CreationModel<FileEntity>
{
    @Contract(pure = true) @Override public @NotNull String toString()
    {
        return "FileCreateModel{" +
                ", filePath='" + filePath + '\'' +
                ", allowedPrivileges=" + privilege + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }

    @Override
    public @NotNull String name()
    {
        return filePath + " " + System.currentTimeMillis();
    }

    @Override
    public @NotNull FileEntity toEntity(@NotNull FileEntity fileEntity) {
        fileEntity.setAuthorId(authorId());
        fileEntity.setFilePath(filePath());
        fileEntity.setPrivilege(Arrays.stream(privilege()).collect(Collectors.toSet()));
        fileEntity.setTags(Arrays.stream(tags()).collect(Collectors.toSet()));
        return fileEntity;
    }
}
