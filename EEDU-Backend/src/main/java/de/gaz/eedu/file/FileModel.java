package de.gaz.eedu.file;

import de.gaz.eedu.entity.model.EntityModel;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public record FileModel(@NotNull Long id,
                        @NotNull String fileName,
                        @NotNull Long authorId,
                        @NotNull String filePath,
                        @NotNull String[] privileges,
                        String[] tags) implements EntityModel
{
    @Override public String toString()
    {
        return "FileModel{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", author=" + authorId +
                ", filePath='" + filePath + '\'' +
                ", allowedPrivileges='" + privileges +
                ", tags=" + tags +
                '}';
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileModel fileModel = (FileModel) o;
        return Objects.equals(id, fileModel.id) && Objects.equals(fileName, fileModel.fileName) && Objects.equals(authorId, fileModel.authorId) && Objects.equals(filePath, fileModel.filePath) && Objects.equals(privileges, fileModel.privileges) && Objects.equals(tags, fileModel.tags);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, fileName, authorId, filePath, Arrays.hashCode(tags));
    }
}
