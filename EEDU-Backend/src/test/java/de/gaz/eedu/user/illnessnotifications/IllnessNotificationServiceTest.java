package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.file.FileEntity;
import de.gaz.eedu.file.FileService;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.stream.Stream;

@Getter(AccessLevel.PROTECTED)
public class IllnessNotificationServiceTest extends ServiceTest<IllnessNotificationService, IllnessNotificationEntity, IllnessNotificationModel, IllnessNotificationCreateModel>
{
    @Autowired private IllnessNotificationService service;
    @Autowired private FileService fileService;

    @Override
    protected @NotNull Eval<IllnessNotificationCreateModel, IllnessNotificationModel> successEval()
    {
        Long timestamp = System.currentTimeMillis();
        FileEntity fileEntity = fileService.getRepository().getReferenceById(2L);
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
