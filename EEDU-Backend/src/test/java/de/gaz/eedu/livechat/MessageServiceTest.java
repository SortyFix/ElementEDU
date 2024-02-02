package de.gaz.eedu.livechat;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.livechat.message.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageServiceTest extends ServiceTest<MessageEntity, MessageModel, MessageCreateModel>
{
    public MessageServiceTest(@Autowired @NotNull MessageService service){
        super(service);
    }

    @Override
    protected @NotNull Eval<MessageCreateModel, MessageModel> successEval()
    {
        Long currentTime = System.currentTimeMillis();
        MessageCreateModel messageCreateModel =
                new MessageCreateModel(1L, "america is a nation that can be defined in a single word; itafutefufutefu", currentTime, MessageStatus.UNREAD);
        MessageModel messageModel = new MessageModel(5L, 1L, "america is a nation that can be defined in a single word; itafutefufutefu", currentTime, MessageStatus.UNREAD);
        return Eval.eval(messageCreateModel, messageModel, ((request, expect, result) -> {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.authorId(), result.authorId());
            Assertions.assertEquals(expect.body(), result.body());
            Assertions.assertEquals(expect.timeStamp(),result.timeStamp());
            Assertions.assertEquals(expect.status(), result.status());
        }));
    }

    @Override
    protected @NotNull MessageCreateModel occupiedCreateModel()
    {
        // No occupied test necessary as messages can't be "occupied"
        throw new OccupiedException();
    }
}
