package de.gaz.eedu.file;

import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileServiceTest
{
    @Autowired private FileRepository fileRepository;
    @Autowired private FileService fileService;
    @Autowired private WebApplicationContext webApplicationContext;

    @Test
    @Transactional
    public void testCreateEntity()
    {
        FileCreateModel fileCreateModel = new FileCreateModel(3L, "roblox.exe", new String[]{"PRIVILEGE_ALL"}, "other", new String[]{"mathe"});
        FileEntity fileEntity = fileService.createEntity(fileCreateModel);
        assertNotNull(fileEntity.getId());
        assertEquals(fileCreateModel.authorId(), fileEntity.getAuthorId());
        assertEquals(fileCreateModel.fileName(), fileEntity.getFileName());
        assertEquals(fileCreateModel.dataDirectory(), fileEntity.getDataDirectory());
        assertTrue(setsContainSameData(fileCreateModel.privilege(), fileEntity.getPrivilege()));
        assertTrue(setsContainSameData(fileCreateModel.tags(), fileEntity.getTags()));
    }

    @Test
    @Transactional
    public void testUpload() throws Exception
    {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World".getBytes()
        );

        FileCreateModel fileCreateModel = new FileCreateModel(2L, "hello.txt", new String[]{"PRIVILEGE_ALL"}, "other", new String[]{"mathe"});
        FileEntity fileEntity = fileService.createEntity(fileCreateModel);

        assertTrue(fileEntity.upload(file));
        assertTrue(Files.exists(Path.of(fileEntity.getFilePath(), file.getOriginalFilename())));
    }

    private <T> boolean setsContainSameData(T[] array, @NotNull Set<T> set)
    {
        Set<T> arraySet = new HashSet<>(Arrays.asList(array));
        return set.equals(arraySet);
    }
}
