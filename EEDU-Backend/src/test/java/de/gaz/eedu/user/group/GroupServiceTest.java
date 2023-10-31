package de.gaz.eedu.user.group;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.GroupService;
import de.gaz.eedu.user.group.model.GroupCreateModel;
import de.gaz.eedu.user.group.model.GroupModel;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import de.gaz.eedu.user.privileges.PrivilegeService;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

public class GroupServiceTest extends ServiceTest<GroupEntity, GroupModel, GroupCreateModel>
{
    private final PrivilegeService privilegeService;

    public GroupServiceTest(@Autowired GroupService service, @Autowired PrivilegeService privilegeService)
    {
        super(service);
        this.privilegeService = privilegeService;
    }

    @Override protected @NotNull Eval<GroupCreateModel, GroupModel> successEval()
    {
        return Eval.eval(new GroupCreateModel("test", new HashSet<>(), new HashSet<>()), new GroupModel(5L, "test",
                new HashSet<>(), new HashSet<>()), (request, expect, result) ->
        {
            Assertions.assertEquals(expect.name(), result.name());
            Assertions.assertEquals(expect.privileges(), result.privileges());
            Assertions.assertEquals(expect.users(), result.users());
        });
    }

    @Override protected GroupCreateModel occupiedCreateModel()
    {
        return new GroupCreateModel("Users", new HashSet<>(), new HashSet<>());
    }

    /**
     * This method tests the functionality of adding a privilege to a group.
     * <p>
     * The purpose of this method is to validate whether the group can be granted certain privileges.
     * It tests for the group with groupID 2 and 1, where the group with groupID 2 is expected to be granted the
     * privilege
     * while the group with groupID 1 is not expected to achieve granting the privilege.
     * <p>
     * The {@link ParameterizedTest} annotation with a custom name helps in identifying individual test cases in logs:
     * <p>
     * 0 => request=2   PASSED<br>
     * 1 => request=1   PASSED
     * </p>
     * This pattern of output assists in quickly pinpointing the failing test case if any occur.
     * <p>
     * The input for the groupIDs is provided by the {@link ValueSource} annotation.
     * The {@link Transactional} annotation is utilized to manage the transactions during each test case, with type
     * set to REQUIRES_NEW,
     * which ensures a new transaction for every method execution.
     *
     * @param groupID the current group id to be tested for granting privilege. These are adjustable in the
     *                {@link ValueSource} annotation.
     */
    @ParameterizedTest(name = "{index} => request={0}") @ValueSource(longs = {2, 3}) @Transactional(Transactional.TxType.REQUIRES_NEW) public void testGroupAddPrivilege(long groupID)
    {
        Eval<PrivilegeEntity, Boolean> eval =
                Eval.eval(privilegeService.loadEntityByID(3).orElseThrow(IllegalStateException::new), groupID == 2,
                        (request1, expect1, result) -> Assertions.assertEquals(expect1, result));

        Tester<PrivilegeEntity, Boolean> tester = (request) ->
                getService().loadEntityByID(groupID).orElseThrow(IllegalStateException::new).grantPrivilege(request);

        test(eval, tester);
    }
}
