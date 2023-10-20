package de.gaz.eedu.user;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.user.exception.InsecurePasswordException;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.GroupService;
import de.gaz.eedu.user.model.UserCreateModel;
import de.gaz.eedu.user.model.UserModel;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

public class UserServiceTest extends ServiceTest<UserEntity, UserModel, UserCreateModel>
{
    private final GroupService groupService;

    public UserServiceTest(@Autowired UserService service, @Autowired GroupService groupService)
    {
        super(service);
        this.groupService = groupService;
    }

    @Override protected @NotNull ServiceTest.Eval<UserCreateModel, UserModel> successEval()
    {
        final UserCreateModel userCreateModel = new UserCreateModel("jonas",
                "yonas",
                "jonas.yonas",
                "Password123!",
                true,
                false);
        final UserModel expected = new UserModel(5L, "jonas",
                "yonas",
                "jonas.yonas",
                true,
                false,
                new HashSet<>());

        return Eval.eval(userCreateModel, expected, (request, expect, result) ->
        {
            Assertions.assertEquals(expect.firstName(), result.firstName());
            Assertions.assertEquals(expect.lastName(), result.lastName());
            Assertions.assertEquals(expect.loginName(), result.loginName());
            Assertions.assertEquals(expect.enabled(), result.enabled());
            Assertions.assertEquals(expect.locked(), result.locked());
            Assertions.assertEquals(expect.groups(), result.groups());
        });
    }

    @Override protected UserCreateModel occupiedEval()
    {
        return new UserCreateModel("Max", "musterman", "max.mustermann", "Password123!", true, false);
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
        Eval<GroupEntity, Boolean> eval =
                Eval.eval(groupService.loadEntityByID(3).orElseThrow(IllegalStateException::new), userID == 2,
                        (request1, expect1, result) -> Assertions.assertEquals(expect1, result));

        Tester<GroupEntity, Boolean> tester = (request) ->
                getService().loadEntityByID(userID).orElseThrow(IllegalStateException::new).attachGroups(request);

        test(eval, tester);
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
                    "jonas",
                    "yonas",
                    "jonas.yonas$",
                    password,
                    true,
                    false);
            Assertions.assertThrows(InsecurePasswordException.class, () -> getService().create(createModel));
        }
    }
}
