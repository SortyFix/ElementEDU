import {Component} from "@angular/core";
import {FormBuilder, Validators} from "@angular/forms";
import {AuthenticationService} from "../authentication.service";
import {LoginData} from "../login-data/login-data";
import {AbstractLoginForm} from "../abstract-login-form";

@Component({standalone: true, template: ''})
export abstract class AbstractCredentialCode extends AbstractLoginForm {

    public constructor(formBuilder: FormBuilder, authenticationService: AuthenticationService) {
        super(formBuilder.group({secret: ['', Validators.required]}), authenticationService);
        this.registerField('secret');
    }

    protected get loginData(): LoginData | undefined {
        return this.authenticationService.loginData;
    }

    protected onCancel(): void {
        this.authenticationService.reset();
    };

    protected override onSubmit(): void {
        const secret: string | undefined = this.secret;
        if (!secret || !this.loginData) {
            return;
        }

        this.executeRequest(secret, this.loginData);
    }

    protected abstract executeRequest(secret: string, loginData: LoginData): void;

    protected get secret(): string | undefined {
        return this.form.get('secret')?.value;
    }
}
