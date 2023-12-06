package de.gaz.eedu.user.privileges;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.model.SimplePrivilegeGroupModel;
import de.gaz.eedu.user.privileges.model.PrivilegeCreateModel;
import de.gaz.eedu.user.privileges.model.PrivilegeModel;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

/**
 * Test class for PrivilegeService.
 * <p>
 * It extends the base class ServiceTest, using PrivilegeEntity as the main entity, PrivilegeModel as the main model,
 * and PrivilegeCreateModel as the creation model for setting up the tests. Helps test PrivilegeService operations.
 *
 * @author ivo
 */
public class PrivilegeServiceTest extends ServiceTest<PrivilegeEntity, PrivilegeModel, PrivilegeCreateModel>
{
    /**
     * Constructs a new PrivilegeServiceTest instance.
     *
     * @param service The PrivilegeService instance to be used in the tests.
     */
    public PrivilegeServiceTest(@Autowired PrivilegeService service)
    {
        super(service);
    }

    @Override protected @NotNull ServiceTest.Eval<PrivilegeCreateModel, PrivilegeModel> successEval()
    {
        PrivilegeCreateModel privilegeCreateModel = new PrivilegeCreateModel("test", new GroupEntity[0]);
        PrivilegeModel privilegeModel = new PrivilegeModel(5L, "TEST", new SimplePrivilegeGroupModel[0]);
        return Eval.eval(privilegeCreateModel, privilegeModel, (request, expect, result) ->
        {
            Assertions.assertEquals(expect.name(), result.name());
            Assertions.assertEquals(expect.groups().length, result.groups().length);
        });
    }

    @Override protected @NotNull PrivilegeCreateModel occupiedCreateModel()
    {
        return new PrivilegeCreateModel("READ", new GroupEntity[0]);
    }
}
