import {Component} from "@angular/core";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AuthenticationService} from "../authentication.service";
import {LoginData} from "../login-data/login-data";
import {AbstractLoginForm} from "../abstract-login-form";

@Component({standalone: true, template: ''})
export abstract class AbstractCredentialForm extends AbstractLoginForm {

    protected constructor(formGroup: FormGroup, authenticationService: AuthenticationService) {
        super(formGroup, authenticationService);
    }

    protected get loginData(): LoginData | undefined {
        return this.authenticationService.loginData;
    }

    protected onCancel(): void {
        this.authenticationService.reset();
    };
}
