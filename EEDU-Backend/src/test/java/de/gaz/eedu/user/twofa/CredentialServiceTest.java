package de.gaz.eedu.user.twofa;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.TestData;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.verification.credentials.CredentialEntity;
import de.gaz.eedu.user.verification.credentials.CredentialService;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import de.gaz.eedu.user.verification.credentials.model.CredentialCreateModel;
import de.gaz.eedu.user.verification.credentials.model.CredentialModel;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.stream.Stream;

@Getter(AccessLevel.PROTECTED)
public class CredentialServiceTest extends ServiceTest<CredentialService, CredentialEntity, CredentialModel, CredentialCreateModel> {

    @Autowired private CredentialService service;

    @Contract(pure = true, value = "-> new")
    private static @NotNull Stream<TestData<CredentialMethod>> getAllowedTwoFactors()
    {
        // skip 4 as it gets deleted
        return Stream.of(new TestData<>(1L, CredentialMethod.EMAIL),
                new TestData<>(2L, CredentialMethod.EMAIL),
                new TestData<>(3L, CredentialMethod.SMS),
                new TestData<>(5L, CredentialMethod.SMS),
                new TestData<>(6L, CredentialMethod.TOTP));
    }

    @Override
    protected @NotNull Eval<CredentialCreateModel, CredentialModel> successEval() {
        CredentialCreateModel twoFactorCreateModel = new CredentialCreateModel(1L, "TOTP", "");
        CredentialModel credentialModel = new CredentialModel(8L, CredentialMethod.TOTP, false, new HashMap<>());
        // some more test values, therefore id 8

        return Eval.eval(twoFactorCreateModel, credentialModel, ((request, expect, result) ->
        {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.method(), result.method());

            Assertions.assertNotNull(result.claims());
            Assertions.assertTrue(result.claims().containsKey("setup"));

            Assertions.assertFalse(result.enabled());
        }));
    }

    @ParameterizedTest(name = "{index} => request={0}") @MethodSource("getAllowedTwoFactors")
    public void testGetAllowedTwoFactors(@NotNull TestData<CredentialMethod> data)
    {
        test(Eval.eval(data.entityID(), data.expected(), Validator.equals()), request ->
        {
            CredentialEntity credentialEntity = getService().loadEntityByIDSafe(request);
            return credentialEntity.getMethod();
        });
    }

    @Override
    protected @NotNull CredentialCreateModel occupiedCreateModel() {
        // do nothing
        throw new OccupiedException();
    }
}
