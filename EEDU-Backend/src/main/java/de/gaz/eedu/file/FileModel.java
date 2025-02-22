package de.gaz.eedu.file;

import de.gaz.eedu.entity.model.EntityModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public record FileModel(@NotNull Long id,
                        @NotNull String dataDirectory,
                        @NotNull String[] privileges,
                        String[] tags) implements EntityModel<Long>
{

    @Contract(pure = true)
    @Override public @NotNull String toString()
    {
        return "FileModel{" +
                "id=" + id +
                ", privileges=" + Arrays.toString(privileges) +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileModel fileModel = (FileModel) o;
        return Objects.equals(id, fileModel.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

    public FileEntity toEntity(@NotNull FileEntity fileEntity)
    {
        fileEntity.setTags(Arrays.stream(tags()).collect(Collectors.toSet()));
        fileEntity.setPrivilege(Arrays.stream(privileges()).collect(Collectors.toSet()));
        fileEntity.setDataDirectory(dataDirectory());
        return fileEntity;
    }
}
