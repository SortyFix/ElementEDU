import { LoginData } from "../login-data/login-data";
import {AbstractCredentialCode} from "./abstract-credential-code";
import {FormBuilder} from "@angular/forms";
import {AuthenticationService} from "../authentication.service";

export class AbstractCredentialSetupCode extends AbstractCredentialCode {

    constructor(formBuilder: FormBuilder, authenticationService: AuthenticationService) {
        super(formBuilder, authenticationService);
    }

    protected override executeRequest(secret: string, loginData: LoginData): void {
        this.authenticationService.enableCredential(secret, loginData).subscribe(this.exceptionHandler('secret'));
    }
}
