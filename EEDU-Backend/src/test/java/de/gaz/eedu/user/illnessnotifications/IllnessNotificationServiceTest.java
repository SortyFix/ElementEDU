package de.gaz.eedu.user.illnessnotifications;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.file.FileEntity;
import de.gaz.eedu.file.FileService;
import de.gaz.eedu.user.illnessnotifications.model.IllnessNotificationCreateModel;
import de.gaz.eedu.user.illnessnotifications.model.IllnessNotificationModel;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;


import java.io.IOException;

@Getter(AccessLevel.PROTECTED)
public class IllnessNotificationServiceTest extends ServiceTest<IllnessNotificationService, IllnessNotificationEntity, IllnessNotificationModel, IllnessNotificationCreateModel>
{
    @Autowired private IllnessNotificationService service;
    @Autowired private FileService fileService;
    @Autowired private IllnessNotificationService illnessNotificationService;

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

    @Test
    @Transactional
    protected void illnessNotificationUploadTest() throws IOException
    {
        MockMultipartFile mockFile = new MockMultipartFile("illnessnotification.txt", getClass().getClassLoader().getResourceAsStream("batchfile1.txt"));
        boolean excused = illnessNotificationService.excuse(1L, "Am sick", 39423943L, mockFile);

        Assertions.assertTrue(excused);
    }

    @Override
    protected @NotNull IllnessNotificationCreateModel occupiedCreateModel()
    {
        throw new OccupiedException();
    }

    @Override
    protected @NotNull TestData<Boolean>[] deleteEntities() {
        return new TestData[] {new TestData<>(3, true) };
    }
}
