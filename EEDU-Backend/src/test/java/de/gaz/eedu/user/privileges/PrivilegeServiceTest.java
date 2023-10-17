package de.gaz.eedu.user.privileges;


import de.gaz.eedu.user.exception.NameOccupiedException;
import de.gaz.eedu.user.privileges.model.PrivilegeCreateModel;
import de.gaz.eedu.user.privileges.model.PrivilegeModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;

/**
 * Test class for the {@link PrivilegeService}.
 * <p>
 * This class tests all functions of the PrivilegeService.
 * These tests are crucial for verifying that all features continue to function as expected after modification.
 * <p>
 * The data generated here is temporary and stored in memory, avoiding interaction with a live database.
 * Rather, an H2 in-memory database is utilized, which is deleted once the tests are done.
 * <p>
 * The annotation {@link ActiveProfiles} sets the current profile to {@code "test"}, instructing
 * Spring Boot to reference the {@code application-test.properties} file instead of the {@code application
 * .properties} file.
 * This is crucial because the test properties adjust the database settings to use an H2 database, which is removed
 * after the tests are run.
 * <p>
 * The {@link TestInstance} annotation is set to {@link org.junit.jupiter.api.TestInstance.Lifecycle#PER_CLASS}.
 * This tells JUnit to create only one instance of this class and reuse it for every method tested,
 * meaning the {@link PrivilegeService} instance is maintained and reused across test methods.
 * <p>
 * This class manages method testing to verify if the code functions as expected.
 * Each test within {@link PrivilegeServiceMockitoTest} performs unit testing to validate the accurate functionality
 * flow within the {@link PrivilegeService}.
 *
 * @author ivo
 * @see PrivilegeServiceMockitoTest
 */
@SpringBootTest @TestInstance(TestInstance.Lifecycle.PER_CLASS) @ActiveProfiles("test") public class PrivilegeServiceTest
{

    private final PrivilegeService privilegeService;

    /**
     * This is the primary constructor for the PrivilegeServiceTest class.
     * <p>
     * It deploys Spring's dependency injection capability through the {@link Autowired} annotation to initialize the
     * PrivilegeService instance.
     * The instantiated PrivilegeService object is aimed to handle operations related to privilege entities, such as
     * creating, updating, and deleting privileges. This service object is expected to be utilized across various
     * test methods in this class.
     * <p>
     * This constructor aids in setting up a suitable test environment by ensuring that the needed service is
     * correctly injected and available for the tests.
     *
     * @param privilegeService a reference to the PrivilegeService instance to carry out the privilege-related
     *                         operations.
     */
    public PrivilegeServiceTest(@Autowired PrivilegeService privilegeService)
    {
        this.privilegeService = privilegeService;
    }

    /**
     * This method presents a test scenario to substantiate the successful creation of a new privilege within the
     * system.
     * <p>
     * The purpose of this test is to conduct an assertive check on the proper execution of the 'create' method of
     * privilegeService. In this scenario, a new privilege with the name "ADMIN" is anticipated to be successfully
     * created.
     * The incentive parameters for the privilege are supplied via a {@link PrivilegeCreateModel} instance.
     * Post creation, the returned privilege parameters are projected to align with the request parameters originally
     * defined in the creation model.
     * <p>
     * Given the pre-defined data scenario including three existing privilege entities, the newly added privilege,
     * namely "ADMIN", is expected to be assigned with the ID value 4.
     * <p>
     * The test comprises three individual assertions to ensure the conformity of the returned privilege's attributes
     * with the expected attributes.
     * These assertions include:
     * - Validating the equality between the returned privilege's ID and its expected value.
     * - Ensuring the name of the returned privilege matches the expected name.
     * - Asserting the consistency between the returned privilege's group entities and the expected group entities.
     *
     * @see #testCreatePrivilegeNameOccupied()
     */
    @Test public void testCreatePrivilegeSuccess()
    {
        PrivilegeCreateModel request = new PrivilegeCreateModel("ADMIN", new HashSet<>());
        PrivilegeModel expected = new PrivilegeModel(4L, "ADMIN", new HashSet<>());

        PrivilegeModel result = privilegeService.create(request);

        Assertions.assertEquals(expected.id(), result.id());
        Assertions.assertEquals(expected.name(), result.name());
        Assertions.assertEquals(expected.groups(), result.groups());
    }

    /**
     * This method carves out a test case scenario to validate the system behavior when attempting to create a
     * privilege with a pre-existing name.
     * <p>
     * The core objective here is verifying the system's response to creating a privilege with the name "READ", which
     * is already registered within the system per the data.sql.
     * The enforcement of unique names for each privilege is a critical practice to uphold a well-functioning and
     * organized system.
     * <p>
     * Within this context, a {@link PrivilegeCreateModel} is prepared with the name set to "READ". As such name is
     * already occupied, an exception of type {@link NameOccupiedException} is anticipated.
     * The string 'expected' contains the same value as the duplicated privilege name, and thus the exception is
     * envisaged to return a name equivalent to 'expected'.
     * <p>
     * If the {@link NameOccupiedException} is not thrown as projected, the test fails, subsequently prompting a
     * failure message stating "The name occupied exception was not thrown.".
     *
     * @see #testCreatePrivilegeSuccess()
     */
    @Test public void testCreatePrivilegeNameOccupied()
    {
        PrivilegeCreateModel request = new PrivilegeCreateModel("READ", new HashSet<>());
        String expected = "READ";

        try
        {
            privilegeService.create(request);
            Assertions.fail("The name occupied exception was not thrown.");
        }
        catch (NameOccupiedException nameOccupiedException)
        {
            Assertions.assertEquals(expected, nameOccupiedException.getName());
        }
    }

    /**
     * This method operates a set of test case scenarios for privilege deletion operations.
     * <p>
     * The aim here is to assert the successful deletion of a privilege with a specific ID. It especially accounts
     * for privilege
     * with the IDs 1 and 20. The deletion of the privilege with the ID 1 is expected to be successful while the
     * privilege with ID 20
     * is anticipated to fail, given that the privilege entities are defined in the data.sql file.
     * <p>
     * A custom named {@link ParameterizedTest} aids in distinguishing failing tests in the test reports, providing a
     * display in the logs as follows:
     * <p>
     * 0 => request=1   PASSED <br>
     * 1 => request=20  PASSED
     * <p>
     * Thus, the nature of these logs aids in easy identification of specific failed tests.
     * <p>
     * The {@link ValueSource} annotation is utilized here for supplying the input values (privilege IDs), which are
     * 1 and 20 in this case.
     * The success of the test case hinges on matching the expected and actual outcomes of the privilege deletion
     * operation as asserted in {@link Assertions#assertEquals(Object, Object)}}.
     *
     * @param request the current privilege ID under test for deletion. Modifications to tested IDs can be made
     *                within the {@link ValueSource} annotation.
     */
    @ParameterizedTest(name = "{index} => request={0}") @ValueSource(longs = {1L, 20L}) public void testDeletePrivilege(long request)
    {
        boolean expected = request == 1;
        boolean result = privilegeService.delete(request);
        Assertions.assertEquals(expected, result);
    }
}
