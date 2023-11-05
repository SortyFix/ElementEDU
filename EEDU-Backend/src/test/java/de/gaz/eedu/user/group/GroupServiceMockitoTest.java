package de.gaz.eedu.user.group;

import de.gaz.eedu.ServiceMockitoTest;
import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.user.group.model.GroupCreateModel;
import de.gaz.eedu.user.group.model.GroupModel;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class GroupServiceMockitoTest extends ServiceMockitoTest<GroupService, GroupEntity, GroupModel, GroupCreateModel>
{
    @Override protected Class<GroupService> serviceClass()
    {
        return GroupService.class;
    }

    @Override protected @NotNull MockitoData<GroupCreateModel, GroupModel> successData()
    {
        GroupCreateModel request = new GroupCreateModel("test", new HashSet<>(), new HashSet<>());
        GroupModel expected = new GroupModel(1L, "test", new HashSet<>(), new HashSet<>());
        return MockitoData.data(request, expected);
    }

    @Override protected @NotNull MockitoData<GroupCreateModel, NameOccupiedException> occupiedData()
    {
        GroupCreateModel request = new GroupCreateModel("admin", new HashSet<>(), new HashSet<>());
        NameOccupiedException expected = new NameOccupiedException("admin");
        return MockitoData.data(request, expected);
    }
}
