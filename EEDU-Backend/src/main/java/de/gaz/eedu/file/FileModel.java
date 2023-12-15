package de.gaz.eedu.file;

import de.gaz.eedu.entity.model.Model;
import jakarta.validation.constraints.NotEmpty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

public record FileModel(@NotNull Long id,
                        @NotNull String fileName,
                        @NotNull Long authorId,
                        @NotNull String filePath,
                        @NotEmpty Set<Long> permittedUsers,
                        @NotEmpty Set<Long> permittedGroups,
                        Set<String> tags) implements Model
{
    @Override public String toString()
    {
        return "FileModel{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", authorId=" + authorId +
                ", filePath='" + filePath + '\'' +
                ", permittedUsers=" + permittedUsers +
                ", permittedGroups=" + permittedGroups +
                ", tags=" + tags +
                '}';
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileModel fileModel = (FileModel) o;
        return Objects.equals(id, fileModel.id) && Objects.equals(fileName, fileModel.fileName) && Objects.equals(authorId, fileModel.authorId) && Objects.equals(filePath, fileModel.filePath) && Objects.equals(permittedUsers, fileModel.permittedUsers) && Objects.equals(permittedGroups, fileModel.permittedGroups) && Objects.equals(tags, fileModel.tags);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, fileName, authorId, filePath, permittedUsers, permittedGroups, tags);
    }
}
