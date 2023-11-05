package de.gaz.eedu.user;

import de.gaz.eedu.ServiceMockitoTest;
import de.gaz.eedu.user.exception.InsecurePasswordException;
import de.gaz.eedu.user.exception.LoginNameOccupiedException;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.model.UserModel;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;

public class UserServiceMockitoTest extends ServiceMockitoTest<UserService, UserEntity, UserModel, UserCreateModel>
{

    @Override protected Class<UserService> serviceClass()
    {
        return UserService.class;
    }

    @Override protected @NotNull MockitoData<UserCreateModel, UserModel> successData()
    {
        UserCreateModel userCreateModel = new UserCreateModel("test", "test", "test", "Password123!", true, false, 1L);
        UserModel userModel = new UserModel(5L, "test", "test", "test", true, false, null /* TODO*/, new HashSet<>());
        return MockitoData.data(userCreateModel, userModel);
    }

    @Override protected @NotNull MockitoData<UserCreateModel, LoginNameOccupiedException> occupiedData()
    {
        UserCreateModel request = new UserCreateModel("test", "test", "max.mustermann", "Password123!", true, false, 1L);
        LoginNameOccupiedException expected = new LoginNameOccupiedException(new UserModel(1L, "max", "mustermann", "max.mustermann", true, false, null, new HashSet<>()));
        return MockitoData.data(request, expected);
    }

    @Test void testCreateUserPasswordInsecureMockito()
    {
        UserCreateModel request = new UserCreateModel("test", "test", "max.mustermann", "password", true, false, 1L);
        InsecurePasswordException expected = new InsecurePasswordException();

        Mockito.when(getEntityService().create(request)).thenThrow(expected);
        try
        {
            getEntityService().create(request);
        }
        catch (InsecurePasswordException insecurePasswordException1)
        {
            Mockito.verify(getEntityService(), Mockito.times(1)).create(request);
        }
    }
}
