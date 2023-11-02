package de.gaz.eedu.user.privileges;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.user.privileges.model.PrivilegeCreateModel;
import de.gaz.eedu.user.privileges.model.PrivilegeModel;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

public class PrivilegeServiceTest extends ServiceTest<PrivilegeEntity, PrivilegeModel, PrivilegeCreateModel>
{
    public PrivilegeServiceTest(@Autowired PrivilegeService service)
    {
        super(service);
    }

    @Override protected @NotNull ServiceTest.Eval<PrivilegeCreateModel, PrivilegeModel> successEval()
    {
        PrivilegeCreateModel privilegeCreateModel = new PrivilegeCreateModel("test", new HashSet<>());
        PrivilegeModel privilegeModel = new PrivilegeModel(5L, "TEST", new HashSet<>());
        return Eval.eval(privilegeCreateModel, privilegeModel, (request, expect, result) ->
        {
            Assertions.assertEquals(expect.name(), result.name());
            Assertions.assertEquals(expect.groups(), result.groups());
        });
    }

    @Override protected @NotNull PrivilegeCreateModel occupiedCreateModel()
    {
        return new PrivilegeCreateModel("READ", new HashSet<>());
    }
}
