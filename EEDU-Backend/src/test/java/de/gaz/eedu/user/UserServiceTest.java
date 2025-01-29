package de.gaz.eedu.user;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.user.exception.InsecurePasswordException;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.GroupService;
import de.gaz.eedu.user.group.model.GroupModel;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.theming.ThemeModel;
import de.gaz.eedu.user.theming.ThemeService;
import de.gaz.eedu.user.verification.credentials.CredentialEntity;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

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
@Getter(AccessLevel.PROTECTED)
public class UserServiceTest extends ServiceTest<UserService, UserEntity, UserModel, UserCreateModel> {
    @Autowired
    private UserService service;
    @Autowired
    private GroupService groupService;
    @Autowired
    private ThemeService themeService;

    @Contract(pure = true)
    private static @NotNull String @NotNull [] testPasswords() {
        return new String[] {
                "password", // no numbers + no uppercase + no special character
                "Password123", // no special character
                "password!", // no numbers + no uppercase
                "password123!", // no uppercase
                "PASSWORD123!", // no lowercase
                "Pa1!!" // to short
        };
    }

    @Override
    protected @NotNull UserCreateModel occupiedCreateModel() {
        return new UserCreateModel("Max", "musterman", "max.mustermann", true, false, UserStatus.PRESENT, 1L, new Long[0]);
    }

    @Override
    protected @NotNull TestData<Boolean>[] deleteEntities() {
        return new TestData[] { new TestData<>(4, true) };
    }

    @Override
    protected @NotNull ServiceTest.Eval<UserCreateModel, UserModel> successEval() {
        final UserCreateModel createModel = new UserCreateModel("jonas", "yonas", "jonas.yonas", true, false, UserStatus.PRESENT, 1L, new Long[0]);
        final ThemeModel themeModel = themeService.loadByIdSafe(1L);
        final UserModel expected = new UserModel(5L, "jonas", "yonas", "jonas.yonas", UserStatus.PRESENT, new GroupModel[0], themeModel);

        return Eval.eval(createModel, expected, (request, expect, result) -> {
            Assertions.assertEquals(expect.firstName(), result.firstName());
            Assertions.assertEquals(expect.lastName(), result.lastName());
            Assertions.assertEquals(expect.loginName(), result.loginName());
        });
    }

    @Transactional
    @ValueSource(longs = {2, 3})
    @ParameterizedTest(name = "{index} => request={0}")
    public void testAttachGroup(long userID) {
        GroupEntity groupEntity = getGroupService().loadEntityById(3).orElseThrow(IllegalStateException::new);
        UserEntity userEntity = getService().loadEntityById(userID).orElseThrow(IllegalStateException::new);

        Runnable test = () -> test(Eval.eval(groupEntity, true, Validator.equals()), userEntity::attachGroups);
        if(userID == 3)
        {
            Assertions.assertThrows(IllegalStateException.class, test::run);
            return;
        }
        test.run();
    }

    /**
     * This method handles the test case scenarios for removing a user from a group.
     * <p>
     * This method verifies the process of removing a user from a specific group. It checks the case for users
     * with userIDs 3 and 2. The user with userID 3 is expected to successfully be removed from the group,
     * whereas the removal of a user with userID 2 is anticipated to fail. The users are present as declared in
     * the data-test.sql file.
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
    @ParameterizedTest(name = "{index} => request={0}")
    @ValueSource(longs = {3, 2})
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void testDetachGroup(long userID) {
        UserEntity userEntity = getService().loadEntityById(userID).orElseThrow(IllegalStateException::new);
        test(Eval.eval(3L /* groupId */, userID == 3, Validator.equals()), userEntity::detachGroups);
    }

    @ParameterizedTest
    @MethodSource("testPasswords")
    public void testInsecurePassword(@NotNull String password) {
        Assertions.assertThrows(InsecurePasswordException.class, () -> {
            CredentialEntity entity = new CredentialEntity();
            entity.setData(password);
            CredentialMethod.PASSWORD.getCredential().creation(entity);
        });
    }
}
