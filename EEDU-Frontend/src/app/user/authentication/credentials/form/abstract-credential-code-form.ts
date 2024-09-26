import {Component} from "@angular/core";
import {FormBuilder, Validators} from "@angular/forms";
import {AbstractCredentialForm} from "./abstract-credential-form";
import {AuthenticationService} from "../../authentication.service";

@Component({standalone: true, template: ''})
export abstract class AbstractCredentialCodeForm extends AbstractCredentialForm {

    public constructor(formBuilder: FormBuilder, authenticationService: AuthenticationService) {
        super(formBuilder.group({secret: ['', Validators.required]}), authenticationService);
        this.registerField('secret');
    }

    protected override onSubmit() {
        const secret: string | undefined = this.secret;
        if (!secret || !this.loginData) {
            return;
        }

        this.authenticationService.verifyCredential(secret, this.loginData).subscribe(this.exceptionHandler('secret'));
    }

    protected get secret(): string | undefined {
        return this.form.get('secret')?.value;
    }
}
