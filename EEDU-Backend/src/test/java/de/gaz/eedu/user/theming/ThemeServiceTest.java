package de.gaz.eedu.user.theming;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.user.model.SimpleUserModel;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

@Getter(AccessLevel.PROTECTED)
public class ThemeServiceTest extends ServiceTest<ThemeService, ThemeEntity, ThemeModel, ThemeCreateModel>
{

    @Autowired private ThemeService service;

    @Override protected @NotNull Eval<ThemeCreateModel, ThemeModel> successEval()
    {
        Set<SimpleUserModel> users = new HashSet<>();
        ThemeCreateModel themeCreateModel = new ThemeCreateModel("test", 0x000000, 0x000000, 0x000000);
        ThemeModel themeModel = new ThemeModel(5L, "test", 0x000000, 0x000000, 0x000000, users);
        return Eval.eval(themeCreateModel, themeModel, (request, expect, result) ->
        {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.name(), result.name());
            Assertions.assertEquals(expect.backgroundColor(), result.backgroundColor());
            Assertions.assertEquals(expect.widgetColor(), result.widgetColor());
            Assertions.assertEquals(expect.textColor(), result.textColor());
        });
    }

    @Override protected @NotNull ThemeCreateModel occupiedCreateModel()
    {
        return new ThemeCreateModel("Light", 0x000000, 0x000000, 0xFFFFFF);
    }
}
