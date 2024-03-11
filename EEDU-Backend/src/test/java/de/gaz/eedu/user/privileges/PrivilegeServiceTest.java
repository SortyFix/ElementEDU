package de.gaz.eedu.user.privileges;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.user.group.GroupEntity;
import de.gaz.eedu.user.group.model.SimplePrivilegeGroupModel;
import de.gaz.eedu.user.privileges.model.PrivilegeCreateModel;
import de.gaz.eedu.user.privileges.model.PrivilegeModel;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Test class for PrivilegeService.
 * <p>
 * It extends the base class ServiceTest, using PrivilegeEntity as the main entity, PrivilegeModel as the main model,
 * and PrivilegeCreateModel as the creation model for setting up the tests. Helps test PrivilegeService operations.
 *
 * @author ivo
 */
@Getter(AccessLevel.PROTECTED)
public class PrivilegeServiceTest extends ServiceTest<PrivilegeService, PrivilegeEntity, PrivilegeModel, PrivilegeCreateModel>
{
    @Autowired private PrivilegeService service;

    @Override protected @NotNull ServiceTest.Eval<PrivilegeCreateModel, PrivilegeModel> successEval()
    {
        PrivilegeCreateModel privilegeCreateModel = new PrivilegeCreateModel("test", new Long[0]);
        PrivilegeModel privilegeModel = new PrivilegeModel(5L, "TEST", new SimplePrivilegeGroupModel[0]);
        return Eval.eval(privilegeCreateModel, privilegeModel, (request, expect, result) ->
        {
            Assertions.assertEquals(expect.name(), result.name());
            Assertions.assertEquals(expect.groups().length, result.groups().length);
        });
    }

    @Override protected @NotNull PrivilegeCreateModel occupiedCreateModel()
    {
        return new PrivilegeCreateModel("READ", new Long[0]);
    }
}
