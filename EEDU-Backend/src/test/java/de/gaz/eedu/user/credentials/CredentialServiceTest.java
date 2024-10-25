package de.gaz.eedu.user.credentials;

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

@Getter(AccessLevel.PROTECTED)
public class CredentialServiceTest extends ServiceTest<CredentialService, CredentialEntity, CredentialModel, CredentialCreateModel> {

    @Autowired
    private CredentialService service;

    @Override
    protected @NotNull Eval<CredentialCreateModel, CredentialModel> successEval() {
        CredentialCreateModel twoFactorCreateModel = new CredentialCreateModel(1L, CredentialMethod.TOTP, "");
        CredentialModel credentialModel = new CredentialModel(995L, CredentialMethod.TOTP, false);

        return Eval.eval(twoFactorCreateModel, credentialModel, ((request, expect, result) -> {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.method(), result.method());
            Assertions.assertFalse(result.enabled());
        }));
    }

    @Override
    protected @NotNull CredentialCreateModel occupiedCreateModel() {
        // do nothing
        throw new OccupiedException();
    }
}
