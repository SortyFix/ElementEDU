package de.gaz.eedu.user.privilege;

import de.gaz.eedu.user.exception.NameOccupiedException;
import de.gaz.eedu.user.privileges.PrivilegeCreateModel;
import de.gaz.eedu.user.privileges.PrivilegeEntity;
import de.gaz.eedu.user.privileges.PrivilegeModel;
import de.gaz.eedu.user.privileges.PrivilegeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;

@SpringBootTest
@ActiveProfiles("test")
public class PrivilegeServiceTest
{
    private PrivilegeService privilegeService;

    @BeforeEach public void init()
    {
        this.privilegeService = Mockito.mock(PrivilegeService.class);
    }

    @Test public void createPrivilegeSuccess()
    {
        PrivilegeCreateModel request = new PrivilegeCreateModel("TEST", new HashSet<>());
        PrivilegeModel response = new PrivilegeModel(4L, "TEST", new HashSet<>());

        Mockito.when(privilegeService.create(request)).thenReturn(response);

        PrivilegeModel result = privilegeService.create(request);

        Mockito.verify(privilegeService, Mockito.times(1)).create(request);

        Assertions.assertEquals(result, response);
    }
}
