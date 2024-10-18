package de.gaz.eedu.user.credentials;

import de.gaz.eedu.ServiceTest;
import de.gaz.eedu.exception.OccupiedException;
import de.gaz.eedu.user.verification.credentials.CredentialEntity;
import de.gaz.eedu.user.verification.credentials.CredentialService;
import de.gaz.eedu.user.verification.credentials.implementations.CredentialMethod;
import de.gaz.eedu.user.verification.credentials.model.CredentialCreateModel;
import de.gaz.eedu.user.verification.credentials.model.CredentialModel;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Getter(AccessLevel.PROTECTED)
public class CredentialServiceTest extends ServiceTest<CredentialService, CredentialEntity, CredentialModel, CredentialCreateModel>
{

    @Autowired private CredentialService service;

    @Override protected @NotNull Eval<CredentialCreateModel, CredentialModel> successEval()
    {
        CredentialCreateModel createModel = new CredentialCreateModel(1L, CredentialMethod.TOTP, false, "");
        CredentialModel credentialModel = new CredentialModel(1055L, CredentialMethod.TOTP, false);

        return Eval.eval(createModel, credentialModel, ((request, expect, result) ->
        {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.method(), result.method());
            Assertions.assertFalse(result.enabled());
        }));
    }

    @Test public void successCreateTemporary()
    {
        CredentialCreateModel createModel = new CredentialCreateModel(1L, CredentialMethod.PASSWORD, true, "Development123!");
        CredentialModel credentialModel = new CredentialModel(962L, CredentialMethod.PASSWORD, true);
        test(Eval.eval(createModel, credentialModel, ((request, expect, result) ->
        {
            Assertions.assertEquals(expect.id(), result.id());
            Assertions.assertEquals(expect.method(), result.method());
            Assertions.assertEquals(expect.enabled(), result.enabled());
        })), getService()::create);
    }

    @Override protected @NotNull CredentialCreateModel occupiedCreateModel()
    {
        // do nothing
        throw new OccupiedException();
    }
}
