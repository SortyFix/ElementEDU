package de.gaz.eedu.user.privileges;

import de.gaz.eedu.ServiceMockitoTest;
import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.model.SimplePrivilegeGroupModel;
import de.gaz.eedu.user.privileges.model.PrivilegeCreateModel;
import de.gaz.eedu.user.privileges.model.PrivilegeModel;
import org.jetbrains.annotations.NotNull;


public class PrivilegeServiceMockitoTest extends ServiceMockitoTest<PrivilegeService, PrivilegeEntity, PrivilegeModel, PrivilegeCreateModel>
{
    @Override protected Class<PrivilegeService> serviceClass()
    {
        return PrivilegeService.class;
    }

    @Override protected @NotNull ServiceMockitoTest.TestExpectation<PrivilegeCreateModel, PrivilegeModel> successData()
    {
        PrivilegeCreateModel request = new PrivilegeCreateModel("test", new GroupEntity[0]);
        PrivilegeModel expected = new PrivilegeModel(4L, "TEST", new SimplePrivilegeGroupModel[0]);
        return TestExpectation.data(request, expected);
    }

    @Override protected @NotNull ServiceMockitoTest.TestExpectation<PrivilegeCreateModel, NameOccupiedException> occupiedData()
    {
        PrivilegeCreateModel request = new PrivilegeCreateModel("READ", new GroupEntity[0]);
        NameOccupiedException expected = new NameOccupiedException("READ");
        return TestExpectation.data(request, expected);
    }
}
