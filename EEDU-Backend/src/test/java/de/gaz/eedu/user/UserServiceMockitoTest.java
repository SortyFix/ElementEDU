package de.gaz.eedu.user;

import de.gaz.eedu.ServiceMockitoTest;
import de.gaz.eedu.user.exception.InsecurePasswordException;
import de.gaz.eedu.user.exception.LoginNameOccupiedException;
import de.gaz.eedu.user.group.model.SimpleUserGroupModel;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorModel;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class UserServiceMockitoTest extends ServiceMockitoTest<UserService, UserEntity, UserModel, UserCreateModel>
{

    @Override protected Class<UserService> serviceClass()
    {
        return UserService.class;
    }

    @Override protected @NotNull ServiceMockitoTest.TestExpectation<UserCreateModel, UserModel> successData()
    {
        UserCreateModel userCreateModel = new UserCreateModel("test", "test", "test", "Password123!", true, false, 1L, UserStatus.PROSPECTIVE);
        UserModel userModel = new UserModel(5L, "test", "test", "test", true, false, new TwoFactorModel[0], null /* TODO*/, new SimpleUserGroupModel[0], UserStatus.PROSPECTIVE);
        return TestExpectation.data(userCreateModel, userModel);
    }

    @Override protected @NotNull ServiceMockitoTest.TestExpectation<UserCreateModel, LoginNameOccupiedException> occupiedData()
    {
        UserCreateModel request = new UserCreateModel("test", "test", "max.mustermann", "Password123!", true, false, 1L, UserStatus.PROSPECTIVE);
        LoginNameOccupiedException expected = new LoginNameOccupiedException(new UserModel(1L, "max", "mustermann", "max.mustermann", true, false, new TwoFactorModel[0], null, new SimpleUserGroupModel[0], UserStatus.PROSPECTIVE));
        return TestExpectation.data(request, expected);
    }

    @Test void testCreateUserPasswordInsecureMockito()
    {
        UserCreateModel request = new UserCreateModel("test", "test", "max.mustermann", "password", true, false, 1L, UserStatus.PROSPECTIVE);
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
