package de.gaz.eedu.user;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.user.exception.InsecurePasswordException;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.GroupService;
import de.gaz.eedu.user.group.model.GroupModel;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.model.UserModel;
import de.gaz.eedu.user.privileges.model.PrivilegeModel;
import de.gaz.eedu.user.theming.ThemeModel;
import de.gaz.eedu.user.theming.ThemeService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
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
@Slf4j
@Getter(AccessLevel.PROTECTED)
public class UserServiceTest extends ServiceTest<Long, UserService, UserEntity, UserModel, UserCreateModel> {

    @Autowired private UserService service;
    @Autowired private GroupService groupService;
    @Autowired private ThemeService themeService;

    private static @NotNull UserCreateModel createModel(int number)
    {
        String id = String.valueOf(number);
        AccountType type = AccountType.STUDENT;
        UserStatus status = UserStatus.PRESENT;

        return new UserCreateModel("User", id, "user." + id, type, false, false, status, 1L, new String[0]);
    }

    private @NotNull UserModel model(int number)
    {
        String id = String.valueOf(number);
        AccountType type = AccountType.STUDENT;
        UserStatus status = UserStatus.PRESENT;
        ThemeModel theme = getThemeService().loadByIdSafe(1L);

        GroupModel[] groupModel = {new GroupModel("student", new PrivilegeModel[0])};
        return new UserModel(number + 1L, "User", id, "user." + id, type, status, groupModel, theme, null);
    }

    @Override
    protected @NotNull UserCreateModel occupiedCreateModel() {
        return createModel(0);
    }

    @Override
    protected @NotNull TestData<Long, Boolean>[] deleteEntities() {

        //noinspection unchecked
        return new TestData[] {
                new TestData<>(17, true),
                new TestData<>(18, true),
                new TestData<>(19, false),
        };
    }

    @Override
    protected @NotNull ServiceTest.Eval<UserCreateModel, UserModel> successEval() {
        return Eval.eval(createModel(18), model(18), (request, expect, result) -> {
            Assertions.assertEquals(expect, result);
            Assertions.assertEquals(expect.firstName(), result.firstName());
            Assertions.assertEquals(expect.lastName(), result.lastName());
            Assertions.assertEquals(expect.loginName(), result.loginName());

            Assertions.assertEquals(expect.accountType(), result.accountType());
            Assertions.assertEquals(expect.status(), result.status());
            Assertions.assertArrayEquals(expect.groups(), result.groups());
            Assertions.assertEquals(expect.theme(), result.theme());
            Assertions.assertNull(result.classroom());
        });
    }

    @Transactional
    @ValueSource(longs = {1, 2})
    @ParameterizedTest(name = "{index} => request={0}")
    public void testAttachGroup(long userID) {
        GroupEntity groupEntity = getGroupService().loadEntityById("group0").orElseThrow(IllegalStateException::new);
        UserEntity userEntity = getService().loadEntityById(userID).orElseThrow(IllegalStateException::new);

        Runnable test = () -> test(Eval.eval(groupEntity, true, Validator.equals()), userEntity::attachGroups);
        if(userID == 1)
        {
            // expect the group was already added
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
     * Similar to the 'testUserAddGroup' method, {@link ParameterizedTest} and a custom id are used to provide
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
    @ValueSource(longs = {1, 2})
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void testDetachGroup(long userID) {
        UserEntity userEntity = getService().loadEntityById(userID).orElseThrow(IllegalStateException::new);
        test(Eval.eval("group0", userID == 1, Validator.equals()), userEntity::detachGroups);
    }
}
