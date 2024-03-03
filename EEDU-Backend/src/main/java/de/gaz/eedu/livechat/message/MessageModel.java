package de.gaz.eedu.livechat.message;

import de.gaz.eedu.entity.model.EntityModel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record MessageModel(@NotNull Long id, @NotNull Long authorId, @NotNull String body,
                           @NotNull Long timeStamp) implements EntityModel
{
    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageModel that = (MessageModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, authorId(), body, timeStamp);
    }
}
