package de.gaz.eedu.user.privileges;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
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
public class PrivilegeServiceTest extends ServiceTest<String, PrivilegeService, PrivilegeEntity, PrivilegeModel, PrivilegeCreateModel>
{
    @Autowired private PrivilegeService service;

    @Override protected @NotNull TestData<String, Boolean>[] deleteEntities()
    {
        //noinspection unchecked
        return new TestData[]{
                new TestData<>("PRIVILEGE9", true),
                new TestData<>("PRIVILEGE10", false)
        };
    }

    @Override protected @NotNull ServiceTest.Eval<PrivilegeCreateModel, PrivilegeModel> successEval()
    {
        PrivilegeCreateModel privilegeCreateModel = new PrivilegeCreateModel("privilege9");
        PrivilegeModel privilegeModel = new PrivilegeModel("PRIVILEGE9");
        return Eval.eval(privilegeCreateModel, privilegeModel, (request, expect, result) ->
            Assertions.assertEquals(expect, result)
        );
    }

    @Override protected @NotNull PrivilegeCreateModel occupiedCreateModel()
    {
        return new PrivilegeCreateModel("PRIVILEGE0");
    }
}
