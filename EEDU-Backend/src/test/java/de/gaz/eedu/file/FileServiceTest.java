package de.gaz.eedu.file;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileServiceTest
{
    @Autowired private FileRepository fileRepository;
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

    private <T> boolean setsContainSameData(T[] array, Set<T> set)
    {
        Set<T> arraySet = new HashSet<>(Arrays.asList(array));
        return set.equals(arraySet);
    }
}
