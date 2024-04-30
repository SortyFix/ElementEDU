package de.gaz.eedu.livechat.chat;

import de.gaz.eedu.entity.model.CreationModel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public record ChatCreateModel(@NotNull Long[] users, @NotNull Long timeOfCreation) implements CreationModel<ChatEntity>
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
}
