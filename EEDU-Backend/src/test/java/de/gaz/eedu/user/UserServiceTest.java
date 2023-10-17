package de.gaz.eedu.user;

import de.gaz.eedu.user.exception.InsecurePasswordException;
import de.gaz.eedu.user.exception.LoginNameOccupiedException;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.GroupService;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.model.UserModel;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static de.gaz.eedu.user.UserTestData.ENABLED;
import static de.gaz.eedu.user.UserTestData.FIRST_NAME;
import static de.gaz.eedu.user.UserTestData.LAST_NAME;
import static de.gaz.eedu.user.UserTestData.LOCKED;
import static de.gaz.eedu.user.UserTestData.LOGIN_NAME;
import static de.gaz.eedu.user.UserTestData.PASSWORD;

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
 * This is necessary as the test properties override the settings for the database to use an H2 database instead,
 * which is deleted after the test have been finished.
 * <p>
 * The {@link TestInstance} annotation uses the Lifecycle
 * {@link org.junit.jupiter.api.TestInstance.Lifecycle#PER_CLASS}.
 * This tells JUnit to create one instance of this class and reuse it for every method it contains. As a result, the
 * {@link UserService} instance does not need to be recreated for each test method.
 * <p>
 * This class handles method testing with the aim to verify if the code still works as expected.
 * {@link UserServiceMockitoTest} performs unit testing to verify the correct functionality flow within the
 * {@link UserService}.
 *
 * @author ivo
 * @see UserServiceMockitoTest
 */
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest
{
    private final UserService userService;
    private final GroupService groupService;

    public UserServiceTest(@Autowired UserService userService, @Autowired GroupService groupService)
    {
        this.userService = userService;
        this.groupService = groupService;
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

        UserCreateModel request = new UserCreateModel(FIRST_NAME, LAST_NAME, LOGIN_NAME, PASSWORD, ENABLED, LOCKED);
        UserModel expected = new UserModel(11L, FIRST_NAME, LAST_NAME, LOGIN_NAME, ENABLED, LOCKED, new HashSet<>());

        UserModel result = userService.create(request);

        Assertions.assertEquals(result, expected);
        Assertions.assertEquals(expected.id(), result.id());
        Assertions.assertEquals(expected.firstName(), result.firstName());
        Assertions.assertEquals(expected.lastName(), result.lastName());
        Assertions.assertEquals(expected.enabled(), result.enabled());
        Assertions.assertEquals(expected.locked(), result.locked());
        Assertions.assertEquals(expected.groups(), result.groups());
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
        UserCreateModel request = new UserCreateModel(FIRST_NAME,
                LAST_NAME,
                "max.mustermann",
                PASSWORD,
                ENABLED,
                LOCKED);
        // de.gaz.sp.UserModel#equals(Object) only tests for the id, therefore any other values are irrelevant.
        UserModel expected = new UserModel(1L, null, null, null, false, false, null);

        try
        {
            userService.create(request);
            Assertions.fail("The login name occupied exception has not been thrown.");
        }
        catch (LoginNameOccupiedException loginNameOccupiedException)
        {
            Assertions.assertEquals(expected, loginNameOccupiedException.getUser());
        }
    }

    /**
     * Test insecure password security.
     * <p>
     * This method tests if the {@link InsecurePasswordException} is thrown when the given password is too weak.
     * Below are some passwords and their reasons why they should fail.
     * <p>
     * A secure password must contain at least one lowercase, one uppercase, one number, one special character, and
     * it must be 6 characters long at least.
     * Otherwise, the password will cause a {@link InsecurePasswordException} as mentioned above.
     */
    @Test public void testCreateUserInsecurePassword()
    {

        for (String password : List.of(
                "password", // no numbers + no uppercase + no special character
                "Password123", // no special character
                "password!", // no numbers + no uppercase
                "password123!", // no uppercase
                "PASSWORD123!", // no lowercase
                "Pa1!!" // to short
        ))
        {
            UserCreateModel createModel = new UserCreateModel(
                    FIRST_NAME,
                    LAST_NAME,
                    LOGIN_NAME + "$",
                    // attach $ as the test already created $LOGIN_NAME. Otherwise, a LoginNameOccupied Exception
                    // would be thrown.
                    password,
                    ENABLED,
                    LOCKED);
            Assertions.assertThrows(InsecurePasswordException.class, () -> userService.create(createModel));
        }
    }

    /**
     * This method handles the test case scenarios for adding a user to a group.
     * <p>
     * This method aims at verifying the process of adding a user to a specific group. It checks the case for the user
     * with userID 3 and 1. The user with userID 3 is expected to successfully get added to the group whereas the
     * userID 1
     * is anticipated to fail. The users are present as declared in the data.sql file.
     * <p>
     * The {@link ParameterizedTest} with a custom name provides better distinction when a test fails as it looks like
     * the following in the logs:
     * <p>
     * 0 => request=3   PASSED<br>
     * 1 => request=1   PASSED (expected)
     * </p>
     * With this test format, identifying which specific test failed becomes simpler.
     * <p>
     * The {@link ValueSource} annotation is used to provide the input values for the tests, the userIDs in this case.
     * The {@link Transactional} annotation is used for setting the transaction management to be used for the test
     * cases,
     * specifically, it sets the value to REQUIRES_NEW which means a new transaction would be initiated for every
     * test case.
     *
     * @param userID the current user id that should be tested for the group addition. These can be modified inside
     *               the {@link ValueSource} annotation.
     */
    @ParameterizedTest(name = "{index} => request={0}") @ValueSource(longs = {3, 1}) @Transactional(Transactional.TxType.REQUIRES_NEW) public void testUserAddGroup(long userID)
    {
        boolean expect = userID == 3;

        Optional<GroupEntity> request = groupService.loadEntityByID(1);
        Assertions.assertTrue(request.isPresent(), "Group 1 does not exist.");

        Optional<UserEntity> userEntity = userService.loadEntityByID(userID);
        Assertions.assertTrue(userEntity.isPresent(), "User 3 does not exist.");

        boolean result = userEntity.get().attachGroups(request.get());

        Assertions.assertEquals(expect, result);
    }

    /**
     * This method tests whether deleting a user works.
     * <p>
     * This method verifies that deleting works as it should. It first tests deleting the user with the ID 1 which
     * should be present
     * as is declared in the data.sql. Then it tries deleting a user with the id 20 which should not exist.
     * <p>
     * The name in the {@link ParameterizedTest} provides better separation when a test fails as it looks like the
     * following in the logs:
     * <p>
     * 0 => request=1   PASSED<br>
     * 1 => request=20  PASSED
     * </p>
     * <p>
     * Therefore, knowing what exactly failed gets easier.
     * The {@link ValueSource} annotation provides the test values.
     *
     * @param request the current user id that should be deleted. These can be changed inside the {@link ValueSource}
     *                annotation.
     */
    @ParameterizedTest(name = "{index} => request={0}")
    @ValueSource(longs = {1L, 20L})
    public void testUserDelete(long request)
    {
        boolean expected = request == 1;
        boolean result = userService.delete(request);
        Assertions.assertEquals(expected, result);
    }
}
