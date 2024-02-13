package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.file.FileEntity;
import de.gaz.eedu.file.FileService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.stream.Stream;

public class IllnessNotificationServiceTest extends ServiceTest<IllnessNotificationEntity, IllnessNotificationModel, IllnessNotificationCreateModel>
{
    private final FileService fileService;
    /**
     * Is a necessary for all children of this class.
     * Most-likely this value is annotated using {@link Autowired} which
     * automatically provides
     * an instance of this {@link EntityService}.
     *
     * @param service which this tests should refer to.
     */
    public IllnessNotificationServiceTest(@NotNull @Autowired IllnessNotificationService service, @NotNull @Autowired
    FileService fileService)
    {
        super(service);
        this.fileService = fileService;
    }

    @Override
    protected @NotNull Eval<IllnessNotificationCreateModel, IllnessNotificationModel> successEval()
    {
        Long timestamp = System.currentTimeMillis();
        FileEntity fileEntity = fileService.loadEntityByIDSafe(2L);
        IllnessNotificationCreateModel createModel = new IllnessNotificationCreateModel(2L,
                "maiau",
                timestamp,
                29312392L,
                fileEntity.getId());
        IllnessNotificationModel model = new IllnessNotificationModel(5L,
                2L,
                IllnessNotificationStatus.PENDING,
                "maiau",
                timestamp,
                29312392L,
                fileEntity.toModel());

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
    protected @NotNull Stream<TestData<Boolean>> deleteEntities() {
        return Stream.of(new TestData<>(3, true));
    }
}
