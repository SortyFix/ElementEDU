package de.gaz.eedu.user.theming;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Getter(AccessLevel.PROTECTED)
public class ThemeServiceTest extends ServiceTest<Long, ThemeService, ThemeEntity, ThemeModel, ThemeCreateModel>
{
    @Autowired private ThemeService service;

    @Override protected @NotNull Eval<ThemeCreateModel, ThemeModel> successEval()
    {
        ThemeCreateModel themeCreateModel = new ThemeCreateModel("test", new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        ThemeModel themeModel = new ThemeModel(7L, "test", (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF);
        return Eval.eval(themeCreateModel, themeModel, (request, expect, result) ->
        {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.name(), result.name());

            Assertions.assertEquals(expect.backgroundColorR(), result.backgroundColorR());
            Assertions.assertEquals(expect.backgroundColorG(), result.backgroundColorG());
            Assertions.assertEquals(expect.backgroundColorB(), result.backgroundColorB());

            Assertions.assertEquals(expect.widgetColorR(), result.widgetColorR());
            Assertions.assertEquals(expect.widgetColorG(), result.widgetColorG());
            Assertions.assertEquals(expect.widgetColorB(), result.widgetColorB());
        });
    }

    @Override protected @NotNull ThemeCreateModel occupiedCreateModel()
    {
        return new ThemeCreateModel("Light", new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
    }

    @Override
    protected @NotNull TestData<Long, Boolean>[] deleteEntities()
    {
        return new TestData[] {new TestData<>(4, true) };
    }
}
