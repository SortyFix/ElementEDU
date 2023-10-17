package de.gaz.eedu.user.group;

import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.user.group.model.GroupCreateModel;
import de.gaz.eedu.user.group.model.GroupModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;

@SpringBootTest
@ActiveProfiles("test")
public class GroupServiceMockitoTest
{
    private GroupService groupService;

    @BeforeEach public void init()
    {
        this.groupService = Mockito.mock(GroupService.class);
    }

    @Test public void testCreatePrivilegeSuccess()
    {
        GroupCreateModel request = new GroupCreateModel("test", new HashSet<>(), new HashSet<>());
        GroupModel response = new GroupModel(1L, "test", new HashSet<>(), new HashSet<>());

        Mockito.when(groupService.create(request)).thenReturn(response);
        groupService.create(request);
        Mockito.verify(groupService, Mockito.times(1)).create(request);
    }

    @Test public void testCreatePrivilegeNameOccupied()
    {
        GroupCreateModel request = new GroupCreateModel("admin", new HashSet<>(), new HashSet<>());
        NameOccupiedException expected = new NameOccupiedException("admin");

        Mockito.when(groupService.create(request)).thenThrow(expected);
        Assertions.assertThrows(NameOccupiedException.class, () -> groupService.create(request));
        Mockito.verify(groupService, Mockito.times(1)).create(request);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    public void testDeletePrivilege(long request)
    {
        boolean expected = request == 1L;

        Mockito.when(groupService.delete(request)).thenReturn(expected);
        groupService.delete(request);
        Mockito.verify(groupService, Mockito.times(1)).delete(request);
    }
}
