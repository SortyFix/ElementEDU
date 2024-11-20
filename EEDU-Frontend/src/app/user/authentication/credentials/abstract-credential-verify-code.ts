import {AbstractCredentialCode} from "./abstract-credential-code";

export class AbstractCredentialVerifyCode extends AbstractCredentialCode {

    protected override executeRequest(secret: string): void {
        this.authenticationService.verifyCredential(secret).subscribe(this.exceptionHandler('secret'));
    }
}
