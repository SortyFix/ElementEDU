package de.gaz.eedu.user.twofa;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.verfication.twofa.TwoFactorEntity;
import de.gaz.eedu.user.verfication.twofa.TwoFactorService;
import de.gaz.eedu.user.verfication.twofa.implementations.TwoFactorMethod;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorCreateModel;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorModel;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class TwoFactorServiceTest extends ServiceTest<TwoFactorEntity, TwoFactorModel, TwoFactorCreateModel> {
    /**
     * Is a necessary for all children of this class.
     * Most-likely this value is annotated using {@link Autowired} which
     * automatically provides
     * an instance of this {@link EntityService}.
     *
     * @param service which this tests should refer to.
     */
    public TwoFactorServiceTest(@Autowired @NotNull TwoFactorService service) {
        super(service);
    }

    @Override
    protected @NotNull Eval<TwoFactorCreateModel, TwoFactorModel> successEval() {
        TwoFactorCreateModel twoFactorCreateModel = new TwoFactorCreateModel(1L, "TOTP", "");
        TwoFactorModel twoFactorModel = new TwoFactorModel(8L, TwoFactorMethod.TOTP, false, new HashMap<>());
        // some more test values, therefore id 8

        return Eval.eval(twoFactorCreateModel, twoFactorModel, ((request, expect, result) ->
        {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.method(), result.method());

            Assertions.assertNotNull(result.claims());
            Assertions.assertTrue(result.claims().containsKey("setup"));

            Assertions.assertFalse(result.enabled());
        }));
    }

    @Override
    protected @NotNull TwoFactorCreateModel occupiedCreateModel() {
        // do nothing
        throw new OccupiedException();
    }

    @Override
    public void testCreateEntityOccupied() {
        // do nothing
    }
}
