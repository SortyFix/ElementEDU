package de.gaz.eedu.file;

import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) @SpringBootTest @ActiveProfiles("test")
public class FileServiceTest
{
    @Autowired private FileService fileService;

    @Test
    @Transactional
    public void testCreateEntity()
    {
        FileCreateModel fileCreateModel = new FileCreateModel("homework", new String[]{"PRIVILEGE_ALL"}, new String[]{"wuff"});;
        FileEntity fileEntity = fileService.createEntity(fileCreateModel);

        assertNotNull(fileEntity.getId());
        assertEquals(fileCreateModel.dataDirectory(), fileEntity.getDataDirectory());
        assertTrue(setsContainSameData(fileCreateModel.privilege(), fileEntity.getPrivilege()));
        assertTrue(setsContainSameData(fileCreateModel.tags(), fileEntity.getTags()));
    }

    // Clean data directory after test. Will implement auto clean soon.
    @Test
    @Transactional
    public void testBatchUpload() throws Exception
    {
        MockMultipartFile[] batch = new MockMultipartFile[]{
                new MockMultipartFile("batchfile1.txt", getClass().getClassLoader().getResourceAsStream("batchfile1.txt")),
                new MockMultipartFile("batchfile2.txt", getClass().getClassLoader().getResourceAsStream("batchfile2.txt")),
                new MockMultipartFile("batchfile3.txt", getClass().getClassLoader().getResourceAsStream("batchfile3.txt"))
        };
        FileCreateModel fileCreateModel = new FileCreateModel("homework", new String[]{"PRIVILEGE_ALL"}, new String[]{"wuff"});
        FileEntity fileEntity = fileService.createEntity(fileCreateModel);
        assertDoesNotThrow(() -> fileEntity.uploadBatch("", batch));
        assertTrue(Arrays.stream(batch).allMatch(mockMultipartFile -> Files.exists(Path.of(fileEntity.getFilePath(), mockMultipartFile.getOriginalFilename()))));
    }

    @Test
    @Transactional
    public void testBatchUploadToSubdirectory() throws Exception
    {
        String subdirectory = "testdir";
        MockMultipartFile[] batch = new MockMultipartFile[]{
                new MockMultipartFile("batchfile1.txt", getClass().getClassLoader().getResourceAsStream("batchfile1.txt")),
                new MockMultipartFile("batchfile2.txt", getClass().getClassLoader().getResourceAsStream("batchfile2.txt")),
                new MockMultipartFile("batchfile3.txt", getClass().getClassLoader().getResourceAsStream("batchfile3.txt"))
        };
        FileCreateModel fileCreateModel = new FileCreateModel("homework", new String[]{"PRIVILEGE_ALL"}, new String[]{"wuff"});;
        FileEntity fileEntity = fileService.createEntity(fileCreateModel);
        assertDoesNotThrow(() -> fileEntity.uploadBatch(subdirectory, batch));
        assertTrue(Arrays.stream(batch).allMatch(mockMultipartFile -> Files.exists(Path.of(fileEntity.getFilePath(subdirectory), mockMultipartFile.getOriginalFilename()))));
    }

    @Test
    @Transactional
    public void testDelete()
    {
        FileCreateModel fileCreateModel = new FileCreateModel("homework", new String[]{"PRIVILEGE_ALL"}, new String[]{"wuff"});;
        FileEntity fileEntity = fileService.createEntity(fileCreateModel);
        assertTrue(fileService.delete(fileEntity.getId(), () -> {}));
    }

    private <T> boolean setsContainSameData(T[] array, @NotNull Set<T> set)
    {
        Set<T> arraySet = new HashSet<>(Arrays.asList(array));
        return set.equals(arraySet);
    }
}
