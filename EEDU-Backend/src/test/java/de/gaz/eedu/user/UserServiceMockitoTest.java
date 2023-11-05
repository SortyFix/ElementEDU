package de.gaz.eedu.user;

import de.gaz.eedu.user.exception.InsecurePasswordException;
import de.gaz.eedu.user.exception.LoginNameOccupiedException;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.theming.ThemeEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;

import static de.gaz.eedu.user.UserTestData.*;


/**
 * Test class that uses Mockito to test the behavior of the {@link UserService}.
 * <p>
 * This class leverages Mockito to create and configure mock objects that simulate the behavior of complex, real
 * objects and replace them within the context of the test environment. This way, Mockito provides the ability to
 * isolate the unit of code under examination and accurately specify its interactions. Therefore, this class tests
 * the behavior of {@link UserService} under various controlled scenarios by defining specific inputs,
 * asserting the
 * expected results, and verifying correct interactions.
 * <p>
 * It's important to note that the individual functions within {@link UserService} are tested in the
 * {@link UserServiceTest} class, while this class primarily focuses on testing the interaction and overall behavior
 * flow of the {@link UserService}.
 *
 * @author Ivo
 * @see UserServiceTest
 */
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceMockitoTest //TODO remove UserTestData
{
    private UserService userService;

    /**
     * Assigns an object to the {@link UserService} class.
     * <p>
     * This method is called before each test is executed and assigns the {@code userService} variable
     * a value. This is necessary to test each userService function provided.
     *
     * @see Mockito#mock(Class)
     * @see #userService
     */
    @BeforeEach public void initMock()
    {
        userService = Mockito.mock(UserService.class);
    }

    /**
     * Tests if the user creation system works.
     * <p>
     * In this test a completely new {@link UserEntity} is created using a defined {@link UserModel}.
     * If the user is not created for some reason the test fails.
     *
     * @see UserService
     */
    @Test public void testCreateUserSuccessTest()
    {

        UserCreateModel request = new UserCreateModel(FIRST_NAME, LAST_NAME, LOGIN_NAME, PASSWORD, ENABLED, LOCKED, 1L);
        UserModel expected = new UserModel(11L, FIRST_NAME, LAST_NAME, LOGIN_NAME, ENABLED, LOCKED, null, new HashSet<>());

        Mockito.when(userService.create(request)).thenReturn(expected);
        userService.create(request);
        Mockito.verify(userService, Mockito.times(1)).create(request);
    }

    /**
     * Tests if the system rejects users with conflicting emails.
     * <p>
     * This method creates a new {@link UserModel} with an email that is already occupied in the database.
     * It should throw an {@link LoginNameOccupiedException} therefore. If it doesn't the test fails.
     * <p>
     * This is the direct contrast to the method {@link #testCreateUserSuccessTest()} as it test if the user creation
     * fails under this specific circumstance.
     *
     * @see #testCreateUserSuccessTest()
     * @see UserService
     */
    @Test public void testCreateUserLoginNameOccupied()
    {
        ThemeEntity themeEntity = new ThemeEntity();
        UserCreateModel request = new UserCreateModel(FIRST_NAME,
                LAST_NAME,
                "max.mustermann",
                PASSWORD,
                ENABLED,
                LOCKED,
                null);
        // de.gaz.sp.UserModel#equals(Object) only tests for the id, therefore any other values are irrelevant.
        UserModel expected = new UserModel(1L, null, null, null, false, false, themeEntity, null);

        Mockito.when(userService.create(request)).thenThrow(new LoginNameOccupiedException(expected));
        Assertions.assertThrows(LoginNameOccupiedException.class, () -> userService.create(request));
        Mockito.verify(userService, Mockito.times(1)).create(request);
    }

    @Test public void testCreateUserInsecurePassword()
    {
        UserCreateModel request = new UserCreateModel(FIRST_NAME,
                LAST_NAME,
                LOGIN_NAME,
                "password",
                ENABLED,
                LOCKED,
                null);

        Mockito.when(userService.create(request)).thenThrow(new InsecurePasswordException());
        Assertions.assertThrows(InsecurePasswordException.class, () -> userService.create(request));
        Mockito.verify(userService, Mockito.times(1)).create(request);
    }
}
