package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.OccupiedException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

public class IllnessNotificationServiceTest extends ServiceTest<IllnessNotificationEntity, IllnessNotificationModel, IllnessNotificationCreateModel>
{
    /**
     * Is a necessary for all children of this class.
     * Most-likely this value is annotated using {@link Autowired} which
     * automatically provides
     * an instance of this {@link EntityService}.
     *
     * @param service which this tests should refer to.
     */
    public IllnessNotificationServiceTest(@NotNull @Autowired IllnessNotificationService service)
    {
        super(service);
    }

    @Override
    protected @NotNull Eval<IllnessNotificationCreateModel, IllnessNotificationModel> successEval()
    {
        Long timestamp = System.currentTimeMillis();
        IllnessNotificationCreateModel createModel = new IllnessNotificationCreateModel(2L, "maiau", timestamp, 29312392L);
        IllnessNotificationModel model = new IllnessNotificationModel(5L, 2L, IllnessNotificationStatus.PENDING, "maiau", timestamp, 29312392L);

        return Eval.eval(createModel, model, ((request, expect, result) -> {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.status(), result.status());
            Assertions.assertEquals(expect.userId(), result.userId());
            Assertions.assertEquals(expect.timestamp(), result.timestamp());
            Assertions.assertEquals(expect.reason(), result.reason());
            Assertions.assertEquals(expect.expirationTime(), result.expirationTime());
        }));
    }

    @Override
    protected @NotNull IllnessNotificationCreateModel occupiedCreateModel()
    {
        throw new OccupiedException();
    }

    @Override
    public void testDeleteEntitySuccess(long id)
    {
        // It is necessary to delete ID 3 as ID 4 has already been deleted
        Assertions.assertEquals(id == 3, getService().delete(id));
    }
}
