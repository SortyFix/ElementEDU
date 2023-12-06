package de.gaz.eedu.user.group;

import de.gaz.eedu.ServiceMockitoTest;
import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.user.group.model.GroupCreateModel;
import de.gaz.eedu.user.group.model.GroupModel;
import de.gaz.eedu.user.model.SimpleUserModel;
import de.gaz.eedu.user.privileges.model.SimplePrivilegeModel;
import org.jetbrains.annotations.NotNull;

public class GroupServiceMockitoTest extends ServiceMockitoTest<GroupService, GroupEntity, GroupModel, GroupCreateModel>
{
    @Override protected Class<GroupService> serviceClass()
    {
        return GroupService.class;
    }

    @Override protected @NotNull ServiceMockitoTest.TestExpectation<GroupCreateModel, GroupModel> successData()
    {
        GroupCreateModel request = new GroupCreateModel("test", false, new Long[0], new Long[0]);
        GroupModel expected = new GroupModel(1L, "test", new SimpleUserModel[0], new SimplePrivilegeModel[0]);
        return TestExpectation.data(request, expected);
    }

    @Override protected @NotNull ServiceMockitoTest.TestExpectation<GroupCreateModel, NameOccupiedException> occupiedData()
    {
        GroupCreateModel request = new GroupCreateModel("admin", false, new Long[0], new Long[0]);
        NameOccupiedException expected = new NameOccupiedException("admin");
        return TestExpectation.data(request, expected);
    }
}
