package de.gaz.eedu.user;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.user.exception.InsecurePasswordException;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.GroupService;
import de.gaz.eedu.user.group.model.SimpleUserGroupModel;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.theming.ThemeEntity;
import de.gaz.eedu.user.theming.ThemeService;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorModel;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * UserServiceTest is a concrete extension of {@link ServiceTest}, which is specifically
 * designed to run tests on {@link UserService}.
 *
 * <p>
 * This class has dependencies on {@link UserService} and {@link GroupService}. These services
 * get auto-wired by the Spring container at runtime, and can be used in the test methods to
 * create different scenarios and to verify the UserService's functionality.
 * </p>
 *
 * @see ServiceTest
 * @see UserService
 * @see GroupService
 */
public class UserServiceTest extends ServiceTest<UserEntity, UserModel, UserCreateModel>
{
    private final GroupService groupService;
    private final ThemeService themeService;

    public UserServiceTest(@Autowired @NotNull UserService service, @Autowired @NotNull GroupService groupService,
            @Autowired @NotNull ThemeService themeService)
    {
        super(service);
        this.groupService = groupService;
        this.themeService = themeService;
    }

    @Override protected @NotNull ServiceTest.Eval<UserCreateModel, UserModel> successEval()
    {
        final UserCreateModel createModel = new UserCreateModel("jonas", "yonas", "jonas.yonas", "Password123!", true
                , false, 1L, UserStatus.PRESENT);
        final UserModel expected = new UserModel(5L, "jonas", "yonas", "jonas.yonas", true, false,
                new TwoFactorModel[0],
                themeService.loadEntityByID(1L).map(ThemeEntity::toSimpleModel).orElseThrow(IllegalStateException::new), new SimpleUserGroupModel[0], UserStatus.PRESENT);

        return Eval.eval(createModel, expected, (request, expect, result) ->
        {
            Assertions.assertEquals(expect.firstName(), result.firstName());
            Assertions.assertEquals(expect.lastName(), result.lastName());
            Assertions.assertEquals(expect.loginName(), result.loginName());
            Assertions.assertEquals(expect.enabled(), result.enabled());
            Assertions.assertEquals(expect.locked(), result.locked());
            Assertions.assertEquals(expect.groups(), result.groups());
            Assertions.assertEquals(expect.theme(), result.theme());
        });
    }

    @Override protected @NotNull UserCreateModel occupiedCreateModel()
    {
        return new UserCreateModel("Max", "musterman", "max.mustermann", "Password123!", true, false, 1L, UserStatus.PRESENT);
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
    @ParameterizedTest(name = "{index} => request={0}") @ValueSource(longs = {2, 3}) @Transactional(Transactional.TxType.REQUIRES_NEW) public void testUserAddGroup(long userID)
    {
        GroupEntity groupEntity = groupService.loadEntityByID(3).orElseThrow(IllegalStateException::new);
        UserEntity userEntity = getService().loadEntityByID(userID).orElseThrow(IllegalStateException::new);

        test(Eval.eval(groupEntity, userID == 2, Validator.equals()), userEntity::attachGroups);
    }

    /**
     * This method handles the test case scenarios for removing a user from a group.
     * <p>
     * This method verifies the process of removing a user from a specific group. It checks the case for users
     * with userIDs 3 and 2. The user with userID 3 is expected to successfully be removed from the group,
     * whereas the removal of a user with userID 2 is anticipated to fail. The users are present as declared in
     * the data.sql file.
     * <p>
     * Similar to the 'testUserAddGroup' method, {@link ParameterizedTest} and a custom name are used to provide
     * better clarity in the logs when a test fails, and the {@link ValueSource} annotation provides the input
     * values for the tests.
     * <p>
     * The {@link Transactional} annotation configures the transaction management for the test cases,
     * specifically, it sets the value to REQUIRES_NEW which means a new transaction would be initiated for every
     * test case.
     *
     * @param userID the current user id that should be tested for the group removal. These IDs can be modified inside
     *               the {@link ValueSource} annotation.
     */
    @ParameterizedTest(name = "{index} => request={0}") @ValueSource(longs = {3, 2}) @Transactional(Transactional.TxType.REQUIRES_NEW) public void testUserDetachGroup(long userID)
    {
        UserEntity userEntity = getService().loadEntityByID(userID).orElseThrow(IllegalStateException::new);
        test(Eval.eval(3L /* groupId */, userID == 3, Validator.equals()), userEntity::detachGroups);
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
        for (String password : List.of("password", // no numbers + no uppercase + no special character
                "Password123", // no special character
                "password!", // no numbers + no uppercase
                "password123!", // no uppercase
                "PASSWORD123!", // no lowercase
                "Pa1!!" // to short
        ))
        {
            UserCreateModel createModel = generatePasswordModel(password);
            Assertions.assertThrows(InsecurePasswordException.class, () -> getService().create(createModel));
        }
    }

    /**
     * Generates a new instance of {@link UserCreateModel} with given password and with
     * predefined user details.
     *
     * <p>
     * This method is annotated with {@code @Contract(value = "_ -> new", pure = true)}. The contract
     * indicates that for any input (denoted by '_'), a new instance is returned implying that it
     * doesn't return {@code null}, and since the method is pure (has no side-effects or dependencies
     * on mutable state), it can be safely called at any time.
     * </p>
     *
     * <p>
     * The value of parameters for {@code UserCreateModel} constructor are predifined:
     * {@code "jonas", "yonas", "jonas.yonas$", password, true, false}.
     * </p>
     *
     * @param password the password for the {@code UserCreateModel} instance.
     * @return a new instance of {@code UserCreateModel} with a given password and with
     * predefined user details.
     * @see UserCreateModel
     */
    @Contract(value = "_ -> new", pure = true) private @NotNull UserCreateModel generatePasswordModel(@NotNull String password)
    {
        return new UserCreateModel("jonas", "yonas", "jonas.yonas$", password, true, false, 1L, UserStatus.PRESENT);
    }
}
