package de.gaz.eedu.file.model;

import jakarta.validation.constraints.NotNull;

public record FileInfoModel(@NotNull int index, @NotNull String fileName, @NotNull Long timeOfModification, @NotNull Long fileSize)
{
    @Override public String toString()
    {
        return "FileInfoModel{" +
                "index=" + index +
                ", fileName='" + fileName + '\'' +
                ", timeOfModification=" + timeOfModification +
                ", fileSize=" + fileSize +
                '}';
    }
}
