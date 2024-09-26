package de.gaz.eedu.user.theming;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

@Getter(AccessLevel.PROTECTED)
public class ThemeServiceTest extends ServiceTest<ThemeService, ThemeEntity, ThemeModel, ThemeCreateModel>
{

    @Autowired private ThemeService service;

    @Override protected @NotNull Eval<ThemeCreateModel, ThemeModel> successEval()
    {
        ThemeCreateModel themeCreateModel = new ThemeCreateModel("test", new short[]{1, 2, 3}, new short[]{4, 5, 6});
        ThemeModel themeModel = new ThemeModel(5L, "test", 1, 2, 3, 4, 5, 6);
        return Eval.eval(themeCreateModel, themeModel, (request, expect, result) ->
        {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.name(), result.name());

            Assertions.assertEquals(expect.backgroundColor_r(), result.backgroundColor_r());
            Assertions.assertEquals(expect.backgroundColor_g(), result.backgroundColor_g());
            Assertions.assertEquals(expect.backgroundColor_b(), result.backgroundColor_b());

            Assertions.assertEquals(expect.widgetColor_r(), result.widgetColor_r());
            Assertions.assertEquals(expect.widgetColor_g(), result.widgetColor_g());
            Assertions.assertEquals(expect.widgetColor_b(), result.widgetColor_b());
        });
    }

    @Override protected @NotNull ThemeCreateModel occupiedCreateModel()
    {
        return new ThemeCreateModel("Light", new short[]{255, 255, 255}, new short[]{255, 255, 255});
    }

    @Override
    protected @NotNull TestData<Boolean>[] deleteEntities()
    {
        return new TestData[] {new TestData<>(4, true) };
    }
}
