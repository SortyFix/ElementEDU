package de.gaz.eedu.file;

import jakarta.transaction.Transactional;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileServiceTest
{
    @Autowired private FileService fileService;

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
        MockMultipartFile partFile = new MockMultipartFile("dummyfile.txt", getClass().getClassLoader().getResourceAsStream("dummyfile.txt"));
        FileCreateModel fileCreateModel = new FileCreateModel(2L, "dummyfile.txt", new String[]{"PRIVILEGE_ALL"}, "other", new String[]{"mathe"});
        FileEntity fileEntity = fileService.createEntity(fileCreateModel);

        assertTrue(fileEntity.upload(partFile));
        assertTrue(Files.exists(Path.of(fileEntity.getFilePath(), partFile.getOriginalFilename())));
    }

    @AfterAll
    public static void onExit() throws IOException
    {
        File directory = new File("/data");
        if(directory.exists()){
            FileUtils.deleteDirectory(new File("/data"));
        }
    }

    private <T> boolean setsContainSameData(T[] array, @NotNull Set<T> set)
    {
        Set<T> arraySet = new HashSet<>(Arrays.asList(array));
        return set.equals(arraySet);
    }
}
