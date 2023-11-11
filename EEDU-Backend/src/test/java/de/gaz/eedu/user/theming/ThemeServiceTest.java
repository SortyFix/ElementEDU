package de.gaz.eedu.user.theming;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.user.model.SimpleUserModel;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

public class ThemeServiceTest extends ServiceTest<ThemeEntity, ThemeModel, ThemeCreateModel>
{

    /**
     * Is a necessary for all children of this class.
     * Most-likely this value is annotated using {@link Autowired} which
     * automatically provides
     * an instance of this {@link EntityService}.
     *
     * @param service which this tests should refer to.
     */
    public ThemeServiceTest(@Autowired @NotNull ThemeService service)
    {
        super(service);
    }

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
