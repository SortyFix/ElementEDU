package de.gaz.eedu.user.group;

import de.gaz.eedu.exception.NameOccupiedException;
import de.gaz.eedu.user.group.model.GroupCreateModel;
import de.gaz.eedu.user.group.model.GroupModel;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import de.gaz.eedu.user.privileges.PrivilegeService;
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
import java.util.Optional;

/**
 * This is the test class for {@link GroupService}
 * <p>
 * It tests all functions of the GroupService to ensure they perform as expected when any changes occur.
 * <p>
 * Note that this test uses a temporary in-memory database and does not access a real
 * production database. It uses an H2 database that is cleared after the test execution is complete.
 * <p>
 * The {@link ActiveProfiles} annotation sets the current profile to {@code "test"}, prompting
 * Spring Boot to use the settings defined in {@code application-test.properties} instead of {@code application
 * .properties}.
 * This is necessary to override the database settings and use an H2 database for testing purposes.
 * <p>
 * The {@link TestInstance} annotation uses the Lifecycle
 * {@link org.junit.jupiter.api.TestInstance.Lifecycle#PER_CLASS}.
 * This tells JUnit to create one instance of this test class and reuse it for all methods it contains. As a result,
 * the {@link GroupService} instance is not repeatedly instantiated for each test method.
 * <p>
 * This class focuses on method testing to ensure the GroupService remains functional as intended.
 *
 * @author ivo
 * @see GroupService
 */
@SpringBootTest @ActiveProfiles("test") @TestInstance(TestInstance.Lifecycle.PER_CLASS) public class GroupServiceTest
{
    private final GroupService groupService;
    private final PrivilegeService privilegeService;

    /**
     * This is the main constructor for the GroupServiceTest class.
     * <p>
     * It utilizes Spring's dependency injection capability via the {@link Autowired} annotation to instantiate and
     * set the GroupService and PrivilegeService objects. These service objects are expected to be utilized in
     * various test methods in this class.
     * <p>
     * The GroupService object is designed to handle all the operations and business logic associated with groups
     * within the system, such as creating, updating, and deleting groups. The PrivilegeService object, on the other
     * hand, primarily manages functionalities related to the privileges entities within the system, ensuring their
     * proper assignment and revocation among groups.
     *
     * @param groupService     A reference to the GroupService instance in order to perform group related operations.
     * @param privilegeService A reference to the PrivilegeService instance to manage the allocation of privileges.
     */
    public GroupServiceTest(@Autowired GroupService groupService,
                            @Autowired PrivilegeService privilegeService)
    {
        this.groupService = groupService;
        this.privilegeService = privilegeService;
    }

    /**
     * This test method is designed to verify the successful creation of a new group within the system.
     * <p>
     * The mechanism under test involves the use of a {@link GroupCreateModel} to forge a new group with the proposed
     * name "Test".
     * The execution of the 'create' method within the {@code groupService} is expected to yield the newly formed
     * group, carrying attributes matching those provided in the initiating model.
     * Considering the pre-set scenario in data.sql, that already includes two groups, the newly added group is
     * anticipated to adopt the id value of 3.
     * Thus, the test will employ a variety of assertions to examine the integrity of each datum returned in the
     * generated group model.
     * Each output value is compared to its equivalent in the pre-packaged expected model.
     * <p>
     * The verification phase includes assessments on:
     * - the coherency of the returned group's id to the expected one.
     * - the coherency of the returned group's name to the expected one.
     * - the coherency of the returned group user entities collection and the expected entities.
     * - the coherency of the returned group privilege entities collection and the expected entities.
     * <p>
     * The accurate behavior of this operation is critical in preserving the functionality of the group management
     * module, thus, the importance of this test case.
     */
    @Test public void testCreateGroupSuccess()
    {
        GroupCreateModel request = new GroupCreateModel("Test", new HashSet<>(), new HashSet<>());
        GroupModel expected = new GroupModel(3L, "Test", new HashSet<>(), new HashSet<>());

        GroupModel result = groupService.create(request);

        Assertions.assertEquals(expected.id(), result.id());
        Assertions.assertEquals(expected.name(), result.name());
        Assertions.assertEquals(expected.userEntities(), result.userEntities());
        Assertions.assertEquals(expected.privileges(), result.privileges());
    }

    /**
     * This method provides a test scenario for assessing the functionality of the system when it comes to creating a
     * new group with a name that is already in use.
     * <p>
     * Specifically, this test aims to verify the system's response when confronted with the creation of a group
     * under the same name as a pre-existing group within the system.
     * The enforcement of unique names for each group is an essential part of maintaining a reliable and organized
     * system.
     * <p>
     * The mechanism under test is a {@link GroupCreateModel} which is structured with the group name set to {@code
     * Admins}.
     * In the context of this test, such a group is already provided by the data.sql script, assuring that the tested
     * use-case scenario is properly set-up.
     * Therefore, an exception of type {@link NameOccupiedException} is anticipated and tested in the assertions
     * provided in this method.
     * The string {@code expected} contains the same value as the name of the attempted duplicate group.
     * Consequently, the exception is expected to return a name that matches the {@code expected} string.
     * <p>
     * If the {@link NameOccupiedException} is not thrown as expected, then the test is marked as a failure. In such
     * a case, an explicit failure message is thrown stating {@code The name occupied exception was not thrown.}.
     * <p>
     * This method is a companion to, and should be executed alongside the {@link #testCreateGroupSuccess()} test to
     * ensure comprehensive coverage of group creation scenarios.
     *
     * @see #testCreateGroupSuccess()
     */
    @Test public void testCreateGroupNameOccupied()
    {
        GroupCreateModel request = new GroupCreateModel("Users", new HashSet<>(), new HashSet<>());
        String expected = "Users";

        try
        {
            groupService.create(request);
            Assertions.fail("The name occupied exception was not thrown.");
        }
        catch (NameOccupiedException nameOccupiedException)
        {
            Assertions.assertEquals(expected, nameOccupiedException.getName());
        }
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
    @ParameterizedTest(name = "{index} => request={0}") @ValueSource(longs = {2, 1}) @Transactional(Transactional.TxType.REQUIRES_NEW) public void testGroupAddPrivilege(long groupID)
    {
        boolean expect = groupID == 2;

        Optional<PrivilegeEntity> privilegeEntity = privilegeService.loadEntityByID(2);
        Assertions.assertTrue(privilegeEntity.isPresent());

        Optional<GroupEntity> groupEntity = groupService.loadEntityByID(groupID);
        Assertions.assertTrue(groupEntity.isPresent());

        boolean result = groupEntity.get().grantPrivilege(privilegeEntity.get());
        Assertions.assertEquals(expect, result);
    }

    /**
     * This test method validates the functionality of deleting a privilege.
     * <p>
     * The purpose of this method is to ensure that the privilege deletion process works as expected.
     * It tests deletion for both existing and non-existing privileges.
     * The test is conducted for privilegeID 1 and 20. In the data.sql, privilege with ID 1 exists and privilege with
     * ID 20 doesn't exist.
     * Thus, deletion operation for ID 1 is expected to succeed, while for ID 20 the operation should fail as there
     * is no existing privilege with that ID.
     * <p>
     * Using the {@link ParameterizedTest} annotation with a custom name format helps identify individual test cases
     * in logs.
     * For instance:
     * <p>
     * 0 => request=1L   PASSED<br>
     * 1 => request=20L  PASSED
     * </p>
     * This pattern of output assists in quickly pinpointing the failing test cases if any error occurs.
     * <p>
     * The input for the privilege IDs is provided by the {@link ValueSource} annotation.
     *
     * @param request the privilege IDs to be tested for deletion. These can be adjusted inside the
     *                {@link ValueSource} annotation.
     */
    @ParameterizedTest(name = "{index} => request={0}") @ValueSource(longs = {1L, 20L}) public void testDeletePrivilege(long request)
    {
        boolean expected = request == 1;
        boolean result = groupService.delete(request);
        Assertions.assertEquals(expected, result);
    }
}
