package de.gaz.eedu.user.privileges;

import de.gaz.eedu.ServiceMockitoTest;
import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.user.privileges.model.PrivilegeCreateModel;
import de.gaz.eedu.user.privileges.model.PrivilegeModel;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class PrivilegeServiceMockitoTest extends ServiceMockitoTest<PrivilegeService, PrivilegeEntity, PrivilegeModel, PrivilegeCreateModel>
{
    @Override protected Class<PrivilegeService> serviceClass()
    {
        return PrivilegeService.class;
    }

    @Override protected @NotNull MockitoData<PrivilegeCreateModel, PrivilegeModel> successData()
    {
        PrivilegeCreateModel request = new PrivilegeCreateModel("test", new HashSet<>());
        PrivilegeModel expected = new PrivilegeModel(4L, "TEST", new HashSet<>());
        return MockitoData.data(request, expected);
    }

    @Override protected @NotNull MockitoData<PrivilegeCreateModel, NameOccupiedException> occupiedData()
    {
        PrivilegeCreateModel request = new PrivilegeCreateModel("READ", new HashSet<>());
        NameOccupiedException expected = new NameOccupiedException("READ");
        return MockitoData.data(request, expected);
    }
}
