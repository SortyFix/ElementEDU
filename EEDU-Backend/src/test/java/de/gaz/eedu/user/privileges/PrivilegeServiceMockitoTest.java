package de.gaz.eedu.user.privileges;

import de.gaz.eedu.user.exception.NameOccupiedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;

/**
 * Test class that uses Mockito for testing the behavior of the {@link PrivilegeService}.
 * <p>
 * This class employs Mockito to create and configure mock objects that mimic the behavior of complex, real objects, substituting them within the test environment. Mockito allows the isolation of the code unit under consideration and the precise specification of its interactions. Thus, this class is focused on testing the behavior of {@link PrivilegeService} under numerous controlled conditions by setting specific inputs, asserting the expected outcomes, and validating accurate interactions.
 * <p>
 * It's significant to note that individual methods within {@link PrivilegeService} are tested in the {@link PrivilegeServiceTest} class, while the primary focus of this class is on testing the interaction and overall behavior flow of the {@link PrivilegeService}.
 *
 * @author Ivo
 * @see PrivilegeServiceTest
 */
@SpringBootTest
@ActiveProfiles("test")
public class PrivilegeServiceMockitoTest
{
    private PrivilegeService privilegeService;

    /**
     * Initializes the PrivilegeService mock before each test.
     * <p>
     * This makes sure the global declared {@link PrivilegeService} has a value.
     * This is archived attaching the {@link BeforeEach} annotation in front of the method.
     */
    @BeforeEach public void init()
    {
        this.privilegeService = Mockito.mock(PrivilegeService.class);
    }

    /**
     * Tests the success scenario of the create() method in {@link PrivilegeService}.
     * The mock implementation should return a predefined response when given a specific input model.
     */
    @Test public void testCreatePrivilegeSuccess()
    {
        PrivilegeCreateModel request = new PrivilegeCreateModel("TEST", new HashSet<>());
        PrivilegeModel response = new PrivilegeModel(4L, "TEST", new HashSet<>());

        Mockito.when(privilegeService.create(request)).thenReturn(response);
        privilegeService.create(request);
        Mockito.verify(privilegeService, Mockito.times(1)).create(request);
    }

    /**
     * Tests the scenario when creating a privilege with an occupied name.
     * The mock implementation should throw a NameOccupiedException when given a specific input model.
     */
    @Test public void testCreatePrivilegeNameOccupied()
    {
        PrivilegeCreateModel request = new PrivilegeCreateModel("READ", new HashSet<>());
        NameOccupiedException expected = new NameOccupiedException("READ");

        Mockito.when(privilegeService.create(request)).thenThrow(expected);
        Assertions.assertThrows(NameOccupiedException.class, () -> privilegeService.create(request));
        Mockito.verify(privilegeService, Mockito.times(1)).create(request);
    }

    /**
     * Parameterized test for the delete() method in {@link PrivilegeService}.
     * Tests whether the correct boolean is returned depending on the input.
     * @param request the ID to be passed to the delete method
     */
    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    public void testDeletePrivilege(long request)
    {
        boolean expected = request == 1L;

        Mockito.when(privilegeService.delete(request)).thenReturn(expected);
        privilegeService.delete(request);
        Mockito.verify(privilegeService, Mockito.times(1)).delete(request);
    }
}
