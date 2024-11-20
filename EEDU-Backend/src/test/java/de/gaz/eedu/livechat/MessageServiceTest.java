package de.gaz.eedu.livechat;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.livechat.message.*;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

@Getter(AccessLevel.PROTECTED)
public class MessageServiceTest extends ServiceTest<MessageService, MessageEntity, MessageModel, MessageCreateModel>
{
    @Autowired private MessageService service;

    @Override
    protected @NotNull Eval<MessageCreateModel, MessageModel> successEval()
    {
        Long currentTime = System.currentTimeMillis();
        MessageCreateModel messageCreateModel =
                new MessageCreateModel(1L, "america is a nation that can be defined in a single word; itafutefufutefu", currentTime);
        MessageModel messageModel = new MessageModel(5L, 1L, "america is a nation that can be defined in a single word; itafutefufutefu", currentTime);
        return Eval.eval(messageCreateModel, messageModel, ((request, expect, result) -> {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.authorId(), result.authorId());
            Assertions.assertEquals(expect.body(), result.body());
            Assertions.assertEquals(expect.timeStamp(),result.timeStamp());
        }));
    }

    @Override
    protected @NotNull MessageCreateModel occupiedCreateModel()
    {
        // No occupied test necessary as messages can't be "occupied"
        throw new OccupiedException();
    }

    @Override
    protected @NotNull TestData<Boolean>[] deleteEntities()
    {
        return new TestData[] {new TestData<>(4, true)};
    }
}
