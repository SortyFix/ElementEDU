package de.gaz.eedu.user.group;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.user.group.model.GroupCreateModel;
import de.gaz.eedu.user.group.model.GroupModel;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import de.gaz.eedu.user.privileges.PrivilegeRepository;
import de.gaz.eedu.user.privileges.model.PrivilegeModel;
import de.gaz.eedu.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * Test class for GroupService.
 * <p>
 * It extends from the base class ServiceTest, using GroupEntity as the main entity, GroupModel as the main model,
 * and GroupCreateModel as the creation model for setting up the tests.
 * <p>
 * The PrivilegeService component is autowired and used in this test class to perform operations related to privileges
 * while testing group service operations.
 *
 * @author ivo
 */
@Getter(AccessLevel.PROTECTED)
public class GroupServiceTest extends ServiceTest<String, GroupService, GroupEntity, GroupModel, GroupCreateModel>
{
    @Autowired private GroupService service;
    @Autowired private PrivilegeRepository privilegeRepository;
    @Autowired private UserRepository userRepository;

    @Override protected @NotNull TestData<String, Boolean>[] deleteEntities()
    {
        //noinspection unchecked
        return new TestData[]{
                new TestData<>("group9", true),
                new TestData<>("group10", false),
        };
    }

    @Transactional
    @Override protected @NotNull Validator<String, Boolean> deletePipeline(@NotNull TestData<String, Boolean> data)
    {
        boolean expect = Objects.equals(data.entityID(), "group9");
        Assertions.assertEquals(expect, getUserRepository().findEntity(10L).orElseThrow().inGroup(data.entityID()));

        PrivilegeEntity before = getPrivilegeRepository().findByIdEagerly("PRIVILEGE8").orElseThrow();
        Assertions.assertEquals(expect, before.getGroupEntities().stream().anyMatch(groupEntity ->
        {
            String groupId = groupEntity.getId();
            return Objects.equals(groupId, data.entityID());
        }));

        return (request, expect1, result) -> {
            Assertions.assertFalse(getUserRepository().findEntity(10L).orElseThrow().inGroup(data.entityID()));

            PrivilegeEntity after = getPrivilegeRepository().findByIdEagerly("PRIVILEGE8").orElseThrow();
            Assertions.assertFalse(after.getGroupEntities().stream().anyMatch(groupEntity ->
            {
                String groupId = groupEntity.getId();
                return Objects.equals(groupId, data.entityID());
            }));
        };
    }

    @Override protected @NotNull Eval<GroupCreateModel, GroupModel> successEval()
    {
        GroupCreateModel groupCreateModel = new GroupCreateModel("group9", new String[0]);
        GroupModel groupModel = new GroupModel("group9", new PrivilegeModel[0]);
        return Eval.eval(groupCreateModel, groupModel, (request, expect, result) ->
        {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertArrayEquals(expect.privileges(), result.privileges());
        });
    }

    @Override protected @NotNull GroupCreateModel occupiedCreateModel()
    {
        return new GroupCreateModel("group0", new String[0]);
    }

    /**
     * This method tests the functionality of adding a privilege to a group.
     * <p>
     * The purpose of this method is to validate whether the group can be granted certain privileges.
     * It tests for the group with groupID 2 and 1, where the group with groupID 2 is expected to be granted the
     * privilege
     * while the group with groupID 1 is not expected to achieve granting the privilege.
     * <p>
     * The {@link ParameterizedTest} annotation with a custom id helps in identifying individual test cases in logs:
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
    @ParameterizedTest(name = "{index} => request={0}") @ValueSource(strings = {"group0", "group0"})
    @Transactional public void testGroupGrantPrivilege(@NotNull String groupID)
    {
        PrivilegeEntity privilege = getPrivilegeRepository().findById("PRIVILEGE0").orElseThrow();
        GroupEntity group = getService().loadEntityById(groupID).orElseThrow();

        Runnable test = () -> test(Eval.eval(privilege, true, Validator.equals()), group::grantPrivilege);
        if(Objects.equals(groupID, "group0"))
        {
            // expect the privilege was already added
            Assertions.assertThrows(IllegalStateException.class, test::run);
            return;
        }
        test.run();
    }

    /**
     * This method tests the functionality of removing a privilege from a group.
     * <p>
     * The intent of the test case is to validate if a group can have certain privileges revoked.
     * It verifies this for groups with groupIDs 3 and 2. The group with groupID 3 is expected to have the privilege
     * successfully revoked while the one with groupID 2 is anticipated to fail in the revocation process.
     * <p>
     * Like the 'testGroupGrantPrivilege' method, this method uses {@link ParameterizedTest} with a custom id
     * for better clarity in the logs in case a test fails, and {@link ValueSource} to provide input values.
     * <p>
     * The {@link Transactional} annotation is used to ensure independent transactions for each method execution,
     * by setting up a new transaction scope for every test case.
     *
     * @param groupID the current group id that should be tested for the privilege revocation. These IDs can be modified inside
     *                the {@link ValueSource} annotation.
     */
    @ParameterizedTest(name = "{index} => request={0}") @ValueSource(strings = {"group0", "group1"})
    @Transactional public void testGroupRevokePrivilege(@NotNull String groupID)
    {
        GroupEntity groupEntity = getService().loadEntityById(groupID).orElseThrow();
        test(Eval.eval("PRIVILEGE0", Objects.equals(groupID, "group0"), Validator.equals()), groupEntity::revokePrivilege);
    }
}
