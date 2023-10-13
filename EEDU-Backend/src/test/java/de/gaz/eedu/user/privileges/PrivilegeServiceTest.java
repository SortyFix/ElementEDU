package de.gaz.eedu.user.privileges;


import de.gaz.eedu.user.exception.NameOccupiedException;
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
 * Spring Boot to reference the {@code application-test.properties} file instead of the {@code application.properties} file.
 * This is crucial because the test properties adjust the database settings to use an H2 database, which is removed after the tests are run.
 * <p>
 * The {@link TestInstance} annotation is set to {@link org.junit.jupiter.api.TestInstance.Lifecycle#PER_CLASS}.
 * This tells JUnit to create only one instance of this class and reuse it for every method tested,
 * meaning the {@link PrivilegeService} instance is maintained and reused across test methods.
 * <p>
 * This class manages method testing to verify if the code functions as expected.
 * Each test within {@link PrivilegeServiceMockitoTest} performs unit testing to validate the accurate functionality flow within the {@link PrivilegeService}.
 *
 * @author ivo
 * @see PrivilegeServiceMockitoTest
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class PrivilegeServiceTest
{

    private final PrivilegeService privilegeService;

    public PrivilegeServiceTest(@Autowired PrivilegeService privilegeService)
    {
        this.privilegeService = privilegeService;
    }

    @Test
    public void createPrivilegeSuccess()
    {
        PrivilegeCreateModel request = new PrivilegeCreateModel("ADMIN", new HashSet<>());
        PrivilegeModel expected = new PrivilegeModel(4L, "ADMIN", new HashSet<>());

        PrivilegeModel result = privilegeService.create(request);

        Assertions.assertEquals(expected.id(), result.id());
        Assertions.assertEquals(expected.name(), result.name());
        Assertions.assertEquals(expected.groupEntities(), result.groupEntities());
    }

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

    @ParameterizedTest(name = "{index} => request={0}")
    @ValueSource(longs = {1L, 20L})
    public void testDeletePrivilege(long request)
    {
        boolean expected = request == 1;
        boolean result = privilegeService.delete(request);
        Assertions.assertEquals(expected, result);
    }
}
