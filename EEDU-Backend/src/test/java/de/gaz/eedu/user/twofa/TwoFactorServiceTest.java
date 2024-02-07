package de.gaz.eedu.user.twofa;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.entity.EntityService;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.verfication.twofa.TwoFactorEntity;
import de.gaz.eedu.user.verfication.twofa.TwoFactorService;
import de.gaz.eedu.user.verfication.twofa.implementations.TwoFactorMethod;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorCreateModel;
import de.gaz.eedu.user.verfication.twofa.model.TwoFactorModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.stream.Stream;

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

    @Contract(pure = true, value = "-> new")
    private static @NotNull Stream<TestData<TwoFactorMethod>> getAllowedTwoFactors()
    {
        // skip 4 as it gets deleted
        return Stream.of(new TestData<>(1L, TwoFactorMethod.EMAIL),
                new TestData<>(2L, TwoFactorMethod.EMAIL),
                new TestData<>(3L, TwoFactorMethod.SMS),
                new TestData<>(5L, TwoFactorMethod.SMS),
                new TestData<>(6L, TwoFactorMethod.TOTP));
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

    @ParameterizedTest(name = "{index} => request={0}") @MethodSource("getAllowedTwoFactors")
    public void testGetAllowedTwoFactors(@NotNull TestData<TwoFactorMethod> data)
    {
        test(Eval.eval(data.entityID(), data.expected(), Validator.equals()), request ->
        {
            TwoFactorEntity twoFactorEntity = getService().loadEntityByIDSafe(request);
            return twoFactorEntity.getMethod();
        });
    }

    @Override
    protected @NotNull TwoFactorCreateModel occupiedCreateModel() {
        // do nothing
        throw new OccupiedException();
    }
}
