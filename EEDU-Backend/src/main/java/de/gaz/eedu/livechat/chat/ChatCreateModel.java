package de.gaz.eedu.livechat.chat;

import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public record ChatCreateModel(@NotNull Long[] users, @NotNull Long timeOfCreation) implements CreationModel<Long, ChatEntity>
{

    @Override
    public @NotNull ChatEntity toEntity(@NotNull ChatEntity chatEntity)
    {
        chatEntity.setUsers(Arrays.stream(users()).toList());
        // Is this necessary?
        chatEntity.setMessages(new ArrayList<>());
        chatEntity.setTimeOfCreation(timeOfCreation());
        return chatEntity;
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatCreateModel that = (ChatCreateModel) o;
        return Objects.deepEquals(users, that.users);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(users);
    }
}
