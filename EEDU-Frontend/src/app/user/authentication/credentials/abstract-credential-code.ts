import {Component} from "@angular/core";
import {FormBuilder, Validators} from "@angular/forms";
import {AuthenticationService} from "../authentication.service";
import {LoginData} from "../login-data/login-data";
import {AbstractCredentialForm} from "./abstract-credential-form";

@Component({standalone: true, template: ''})
export abstract class AbstractCredentialCode extends AbstractCredentialForm {

    public constructor(formBuilder: FormBuilder, authenticationService: AuthenticationService) {
        super(formBuilder.group({secret: ['', Validators.required]}), authenticationService);
        this.registerField('secret');
    }

    protected get secret(): string | undefined {
        return this.form.get('secret')?.value;
    }

    protected override onSubmit(): void {

        const secret: string | undefined = this.secret;
        if (!secret || !this.loginData) {
            return;
        }

        this.executeRequest(secret, this.loginData);
    }

    protected abstract executeRequest(secret: string, loginData: LoginData): void;
}
