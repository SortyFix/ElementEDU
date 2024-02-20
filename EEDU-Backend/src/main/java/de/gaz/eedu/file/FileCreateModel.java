package de.gaz.eedu.file;

import de.gaz.eedu.entity.model.CreationModel;
import jakarta.validation.constraints.NotEmpty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public record FileCreateModel(@NotNull Long authorId,
                              @NotNull String fileName,
                              @NotEmpty String[] privilege,
                              @NotNull String dataDirectory,
                              String[] tags) implements CreationModel<FileEntity>
{
    @Contract(pure = true)
    @Override public String toString()
    {
        return "FileCreateModel{" +
                "authorId=" + authorId +
                ", fileName='" + fileName + '\'' +
                ", privilege=" + Arrays.toString(privilege) +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }

    @Override
    public @NotNull String name()
    {
        return authorId() + " " + System.currentTimeMillis();
    }

    @Override
    public @NotNull FileEntity toEntity(@NotNull FileEntity fileEntity) {
        fileEntity.setAuthorId(authorId());
        fileEntity.setFileName(fileName());
        fileEntity.setPrivilege(Arrays.stream(privilege()).collect(Collectors.toSet()));
        fileEntity.setTags(Arrays.stream(tags()).collect(Collectors.toSet()));
        fileEntity.setDataDirectory(dataDirectory());
        return fileEntity;
    }
}
