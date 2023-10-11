package de.gaz.eedu.user;

import de.gaz.eedu.user.exception.LoginNameOccupiedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;

import static de.gaz.eedu.user.UserEntityServiceTest.UserTestData.*;

/**
 * Test for the {@link UserService}
 * <p>
 * This class tests all functions of the user service.
 * This is necessary to check if all functions still work as intended when changing them.
 * <p>
 * Note that the data created here is only temporary in the memory and does not access a real
 * database but instead an H2 which will be deleted after the test is over.
 * <p>
 * The annotation {@link ActiveProfiles} sets the current profile to {@code "test"} which tells
 * Spring Boot to use the {@code application-test.properties} files instead of the {@code application.properties} file.
 * This is necessary as the test properties override the settings for the database to use an H2 database instead, which is deleted after the test have been finished.
 *
 * @author ivo
 */
@SpringBootTest
@ActiveProfiles("test")
public class UserEntityServiceTest {

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
    @Test public void createUserSuccessTest() {

        UserCreateModel userRequest = new UserCreateModel(FIRST_NAME, LAST_NAME, LOGIN_NAME, PASSWORD, ENABLED, LOCKED, new HashSet<>());
        UserModel userResponse = new UserModel(3L, FIRST_NAME, LAST_NAME, LOGIN_NAME, ENABLED, LOCKED, new HashSet<>());

        Mockito.when(userService.create(userRequest)).thenReturn(userResponse);

        UserModel result = userService.create(userRequest);
        Mockito.verify(userService, Mockito.times(1)).create(userRequest);

        Assertions.assertEquals(result, userResponse);
        Assertions.assertEquals(userResponse.id(), result.id());
        Assertions.assertEquals(userResponse.firstName(), result.firstName());
        Assertions.assertEquals(userResponse.lastName(), result.lastName());
        Assertions.assertEquals(userResponse.enabled(), result.enabled());
        Assertions.assertEquals(userResponse.locked(), result.locked());
        Assertions.assertEquals(userResponse.groupEntities(), result.groupEntities());
    }

    /**
     * Tests if the system rejects users with conflicting emails.
     * <p>
     * This method creates a new {@link UserModel} with an email that is already occupied in the database.
     * It should throw an {@link LoginNameOccupiedException} therefore. If it doesn't the test fails.
     * <p>
     * This is the direct contrast to the method {@link #createUserSuccessTest()} as it test if the user creation fails under this specific circumstance.
     *
     * @see #createUserSuccessTest()
     * @see UserService
     */
    @Test public void createUserEmailOccupied() {
        UserCreateModel userRequest = new UserCreateModel(FIRST_NAME, LAST_NAME, "max.mustermann", PASSWORD, ENABLED, LOCKED, new HashSet<>());

        // de.gaz.sp.UserModel#equals(Object) only tests for the id, therefore any other values are irrelevant.
        UserModel userResponse = new UserModel(1L, null, null, null, false, false, null);

        Mockito.when(userService.create(userRequest)).thenThrow(new LoginNameOccupiedException(userResponse));

        try {
            userService.create(userRequest);
            Assertions.fail("The email occupied exception has not been thrown.");
        } catch (LoginNameOccupiedException loginNameOccupiedException) {
            Assertions.assertEquals(loginNameOccupiedException.getUser(), userResponse);
            Mockito.verify(userService, Mockito.times(1)).create(userRequest);
        }
    }

    /**
     * Provides test data that is required to use {@link #createUserSuccessTest()} and {@link #createUserEmailOccupied()}
     * <p>
     * This intern class provides some final values that are required to test the {@link UserService}.
     * Note that these values are final and can be used differently from test to test.
     * They just act as a general guideline to not have to write the same things over and over.
     *
     * @author ivo
     */
    static final class UserTestData
    {
        static final String FIRST_NAME = "John";
        static final String LAST_NAME = "Doe";
        static final String LOGIN_NAME = "john.doe";
        static final String PASSWORD = "password123";
        static final boolean ENABLED = true;
        static final boolean LOCKED = false;
    }
}
