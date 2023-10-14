package de.gaz.eedu.user.group;

import de.gaz.eedu.user.exception.NameOccupiedException;
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
 * Spring Boot to use the settings defined in {@code application-test.properties} instead of {@code application.properties}.
 * This is necessary to override the database settings and use an H2 database for testing purposes.
 * <p>
 * The {@link TestInstance} annotation uses the Lifecycle {@link org.junit.jupiter.api.TestInstance.Lifecycle#PER_CLASS}.
 * This tells JUnit to create one instance of this test class and reuse it for all methods it contains. As a result, the {@link GroupService} instance is not repeatedly instantiated for each test method.
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
     * Constructor for GroupServiceTest.
     * <p>
     * This constructor uses Spring's {@link Autowired} annotation for dependency injection.
     * It sets groupService and privilegeService for later use in test methods.
     *
     * @param groupService     Service to manage groups.
     * @param privilegeService Service to manage privileges.
     */
    public GroupServiceTest(@Autowired GroupService groupService, @Autowired PrivilegeService privilegeService)
    {
        this.groupService = groupService;
        this.privilegeService = privilegeService;
    }

    /**
     * This test method verifies that creating a new group works successfully.
     * <p>
     * A GroupCreateModel instance is used to create a new group with the name "Test".
     * The response of create method from groupService is captured and is expected to return the created group with all attributes as provided in the model.
     * In the provided data.sql, there are two groups and therefore a new group is expected to have id 3.
     * Assertion checks are made on all the values returned in the group model.
     */
    @Test public void testCreateGroupSuccess()
    {
        GroupCreateModel request = new GroupCreateModel("Test", new HashSet<>(), new HashSet<>());
        GroupModel expected = new GroupModel(3L, "Test", new HashSet<>(), new HashSet<>());

        GroupModel result = groupService.create(request);

        Assertions.assertEquals(expected.id(), result.id());
        Assertions.assertEquals(expected.name(), result.name());
        Assertions.assertEquals(expected.userEntities(), result.userEntities());
        Assertions.assertEquals(expected.privilegeEntities(), result.privilegeEntities());
    }

    /**
     * This test method checks if creating a group with a name that's already occupied throws an error.
     * <p>
     * A GroupCreateModel with the name "Admins" is used to simulate the creation of a group using a name that already exists in the data.sql.
     * A try-catch block is used to catch the NameOccupiedException which is expected to be thrown. If it does not get thrown, that indicates a failure of the test and hence, an explicit fail is invoked.
     * In the catch block, it asserts that the name returned in the exception matches the name that was used on creation.
     */
    @Test public void testCreateGroupNameOccupied()
    {
        GroupCreateModel request = new GroupCreateModel("Admins", new HashSet<>(), new HashSet<>());
        String expected = "Admins";

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
     * It tests for the group with groupID 2 and 1, where the group with groupID 2 is expected to be granted the privilege
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
     * The {@link Transactional} annotation is utilized to manage the transactions during each test case, with type set to REQUIRES_NEW,
     * which ensures a new transaction for every method execution.
     *
     * @param groupID the current group id to be tested for granting privilege. These are adjustable in the {@link ValueSource} annotation.
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
     * The test is conducted for privilegeID 1 and 20. In the data.sql, privilege with ID 1 exists and privilege with ID 20 doesn't exist.
     * Thus, deletion operation for ID 1 is expected to succeed, while for ID 20 the operation should fail as there is no existing privilege with that ID.
     * <p>
     * Using the {@link ParameterizedTest} annotation with a custom name format helps identify individual test cases in logs.
     * For instance:
     * <p>
     * 0 => request=1L   PASSED<br>
     * 1 => request=20L  PASSED
     * </p>
     * This pattern of output assists in quickly pinpointing the failing test cases if any error occurs.
     * <p>
     * The input for the privilege IDs is provided by the {@link ValueSource} annotation.
     *
     * @param request the privilege IDs to be tested for deletion. These can be adjusted inside the {@link ValueSource} annotation.
     */
    @ParameterizedTest(name = "{index} => request={0}") @ValueSource(longs = {1L, 20L}) public void testDeletePrivilege(long request)
    {
        boolean expected = request == 1;
        boolean result = groupService.delete(request);
        Assertions.assertEquals(expected, result);
    }
}
