import {AbstractCredentialCode} from "./abstract-credential-code";
import {LoginData} from "../login-data/login-data";

export class AbstractCredentialVerifyCode extends AbstractCredentialCode {

    protected override executeRequest(secret: string, loginData: LoginData): void {
        this.authenticationService.verifyCredential(secret, loginData).subscribe(this.exceptionHandler('secret'));
    }
}
