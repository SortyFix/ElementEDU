package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.exception.OccupiedException;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

@Getter(AccessLevel.PROTECTED)
public class IllnessNotificationServiceTest extends ServiceTest<IllnessNotificationService, IllnessNotificationEntity, IllnessNotificationModel, IllnessNotificationCreateModel>
{
    @Autowired private IllnessNotificationService service;

    @Override
    protected @NotNull Eval<IllnessNotificationCreateModel, IllnessNotificationModel> successEval()
    {
        Long timestamp = System.currentTimeMillis();
        IllnessNotificationCreateModel createModel = new IllnessNotificationCreateModel(2L, timestamp, "meine mutter is auf nen legostein getreten und hat meine ps2 beschädigt");
        IllnessNotificationModel model = new IllnessNotificationModel(5L, 2L, IllnessNotificationStatus.PENDING, timestamp, "meine mutter is auf nen legostein getreten und hat meine ps2 beschädigt");

        return Eval.eval(createModel, model, ((request, expect, result) -> {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.status(), result.status());
            Assertions.assertEquals(expect.userId(), result.userId());
            Assertions.assertEquals(expect.timestamp(), result.timestamp());
            Assertions.assertEquals(expect.reason(), result.reason());
        }));
    }

    @Override
    protected @NotNull IllnessNotificationCreateModel occupiedCreateModel()
    {
        throw new OccupiedException();
    }
}
