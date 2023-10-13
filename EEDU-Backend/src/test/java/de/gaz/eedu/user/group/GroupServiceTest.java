package de.gaz.eedu.user.group;

import de.gaz.eedu.user.exception.NameOccupiedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GroupServiceTest
{

    private final GroupService groupService;

    public GroupServiceTest(@Autowired GroupService groupService)
    {
        this.groupService = groupService;
    }

    @Test
    public void createPrivilegeSuccess()
    {
        GroupCreateModel request = new GroupCreateModel("Test", new HashSet<>(), new HashSet<>());
        GroupModel expected = new GroupModel(3L, "Test", new HashSet<>(), new HashSet<>());

        GroupModel result = groupService.create(request);

        Assertions.assertEquals(expected.id(), result.id());
        Assertions.assertEquals(expected.name(), result.name());
        Assertions.assertEquals(expected.userEntities(), result.userEntities());
        Assertions.assertEquals(expected.privilegeEntities(), result.privilegeEntities());
    }

    @Test public void testCreatePrivilegeNameOccupied()
    {
        GroupCreateModel request = new GroupCreateModel("Admins", new HashSet<>(), new HashSet<>());
        String expected = "Admins";

        try
        {
            groupService.create(request);
            Assertions.fail("The name occupied exception was not thrown.");
        }
        catch (NameOccupiedException nameOccupiedException)
        {
            Assertions.assertEquals(expected, nameOccupiedException.getName());
        }
    }

    @ParameterizedTest(name = "{index} => request={0}")
    @ValueSource(longs = {1L, 20L})
    public void testDeletePrivilege(long request)
    {
        boolean expected = request == 1;
        boolean result = groupService.delete(request);
        Assertions.assertEquals(expected, result);
    }
}
