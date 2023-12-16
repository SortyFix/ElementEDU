package de.gaz.eedu.livechat;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.livechat.message.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageServiceTest extends ServiceTest<MessageEntity, MessageModel, MessageCreateModel>
{
    /**
     * Is a necessary for all children of this class.
     * Most-likely this value is annotated using {@link Autowired} which
     * automatically provides
     * an instance of this {@link EntityService}.
     *
     * @param service which this tests should refer to.
     */

    public MessageServiceTest(@Autowired @NotNull MessageService service)
    {
        super(service);
    }

    @Override
    protected @NotNull Eval<MessageCreateModel, MessageModel> successEval()
    {
        Long authorId = 0L;
        String body = "I have eaten your pet turtle :)";
        Long timestamp = System.currentTimeMillis();
        MessageCreateModel messageCreateModel = new MessageCreateModel(authorId, body);
        MessageModel messageModel = new MessageModel(1L, authorId, body, timestamp, MessageStatus.UNREAD);
        return Eval.eval(messageCreateModel, messageModel, (request, expect, result) -> {
            Assertions.assertEquals(expect.messageId(), result.messageId());
            Assertions.assertEquals(expect.authorId(), result.authorId());
            Assertions.assertEquals(expect.body(), result.body());
            Assertions.assertEquals(expect.timeStamp(), result.timeStamp());
            Assertions.assertEquals(expect.status(), result.status());
        });
    }

    @NotNull
    @Override
    protected MessageCreateModel occupiedCreateModel()
    {
        return new MessageCreateModel(0L, ":)");
    }
}
