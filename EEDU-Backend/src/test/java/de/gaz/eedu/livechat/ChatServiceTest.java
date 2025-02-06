package de.gaz.eedu.livechat;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.livechat.chat.ChatCreateModel;
import de.gaz.eedu.livechat.chat.ChatEntity;
import de.gaz.eedu.livechat.chat.ChatModel;
import de.gaz.eedu.livechat.chat.ChatService;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

@Getter(AccessLevel.PROTECTED)
public class ChatServiceTest extends ServiceTest<ChatService, ChatEntity, ChatModel, ChatCreateModel>
{
    @Autowired private ChatService service;

    @Override
    protected @NotNull Eval<ChatCreateModel, ChatModel> successEval()
    {
        Long[] emptyArr = new Long[0];
        Long currentTime = System.currentTimeMillis();
        ChatCreateModel chatCreateModel = new ChatCreateModel(emptyArr, currentTime);
        // Empty string because no user has been given; therefore no name
        ChatModel chatModel = new ChatModel(5L, "" ,currentTime, emptyArr, emptyArr);
        return Eval.eval(chatCreateModel, chatModel, (request, expect, result) ->
        {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.timeOfCreation(), result.timeOfCreation());
            Assertions.assertEquals(expect.chatTitle(), result.chatTitle());
            Assertions.assertArrayEquals(expect.users(), result.users());
            Assertions.assertArrayEquals(expect.chatHistory(), result.chatHistory());
        });
    }

    @Override
    protected @NotNull ChatCreateModel occupiedCreateModel()
    {
        Long[] users = {1L, 3L};
        return new ChatCreateModel(users, System.currentTimeMillis());
    }

    @Override
    protected @NotNull TestData<Boolean>[] deleteEntities()
    {
        return new TestData[]{new TestData<>(4, true)};
    }
}
