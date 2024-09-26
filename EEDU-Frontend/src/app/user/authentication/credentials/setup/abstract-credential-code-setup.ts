import { LoginData } from "../../login-data/login-data";
import {AbstractCredentialCode} from "../abstract-credential-code";

export class AbstractCredentialCodeSetup extends AbstractCredentialCode {

    protected override executeRequest(secret: string, loginData: LoginData): void {
        this.authenticationService.enableCredential(secret, loginData).subscribe(this.exceptionHandler('secret'));
    }
}
