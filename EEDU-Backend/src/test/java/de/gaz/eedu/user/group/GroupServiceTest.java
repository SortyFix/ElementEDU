package de.gaz.eedu.user.group;

import de.gaz.eedu.ServiceTest;
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

    @Override protected @NotNull GroupCreateModel occupiedCreateModel()
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
    @ParameterizedTest(name = "{index} => request={0}") @ValueSource(longs = {2, 3}) @Transactional(Transactional.TxType.REQUIRES_NEW) public void testGroupGrantPrivilege(long groupID)
    {
        PrivilegeEntity privilegeEntity = privilegeService.loadEntityByID(3).orElseThrow(IllegalStateException::new);
        GroupEntity groupEntity = getService().loadEntityByID(groupID).orElseThrow(IllegalStateException::new);
        test(Eval.eval(privilegeEntity, groupID == 2, Validator.equals()), groupEntity::grantPrivilege);
    }

    /**
     * This method tests the functionality of removing a privilege from a group.
     * <p>
     * The intent of the test case is to validate if a group can have certain privileges revoked.
     * It verifies this for groups with groupIDs 3 and 2. The group with groupID 3 is expected to have the privilege
     * successfully revoked while the one with groupID 2 is anticipated to fail in the revocation process.
     * <p>
     * Like the 'testGroupGrantPrivilege' method, this method uses {@link ParameterizedTest} with a custom name
     * for better clarity in the logs in case a test fails, and {@link ValueSource} to provide input values.
     * <p>
     * The {@link Transactional} annotation is used to ensure independent transactions for each method execution,
     * by setting up a new transaction scope for every test case.
     *
     * @param groupID the current group id that should be tested for the privilege revocation. These IDs can be modified inside
     *                the {@link ValueSource} annotation.
     */
    @ParameterizedTest(name = "{index} => request={0}") @ValueSource(longs = {3, 2}) @Transactional(Transactional.TxType.REQUIRES_NEW) public void testGroupRevokePrivilege(long groupID)
    {
        GroupEntity groupEntity = getService().loadEntityByID(groupID).orElseThrow(IllegalStateException::new);
        test(Eval.eval(3L /* privilegeId */, groupID == 3, Validator.equals()), groupEntity::revokePrivilege);
    }
}
