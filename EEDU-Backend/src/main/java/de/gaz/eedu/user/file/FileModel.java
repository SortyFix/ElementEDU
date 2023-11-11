package de.gaz.eedu.user.file;

import de.gaz.eedu.entity.model.Model;
import de.gaz.eedu.user.UserEntity;
import de.gaz.eedu.user.group.GroupEntity;
import jakarta.validation.constraints.NotEmpty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

public record FileModel(@NotNull Long id,
                        @NotNull String fileName,
                        @NotNull String filePath,
                        @NotEmpty Set<UserEntity> permittedUsers,
                        @NotEmpty Set<GroupEntity> permittedGroups,
                        Set<String> tags) implements Model
{
    @Contract(pure = true) @Override public @NotNull String toString()
    {
        return "FileModel{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", permittedUsers=" + permittedUsers +
                ", permittedGroups=" + permittedGroups +
                ", tags='" + tags + '\'' +
                '}';
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileModel fileModel = (FileModel) o;
        return Objects.equals(id, fileModel.id) && Objects.equals(fileName, fileModel.fileName)
                              && Objects.equals(filePath, fileModel.filePath) && Objects.equals(permittedUsers, fileModel.permittedUsers)
                              && Objects.equals(permittedGroups, fileModel.permittedGroups) && Objects.equals(tags, fileModel.tags);
    }

    @Override
    public int hashCode()
    {
        // Do we need to hash all this?
        return Objects.hash(id, fileName, filePath, permittedUsers, permittedGroups, tags);
    }
}
