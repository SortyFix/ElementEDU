import {Component, Inject} from "@angular/core";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AbstractCredentialForm} from "./abstract-credential-form";
import {AuthenticationService} from "../../authentication.service";

@Component({standalone: true, template: ''})
export abstract class AbstractCredentialCodeForm extends AbstractCredentialForm<{ secret: string }>
{
    public constructor(formBuilder: FormBuilder, authenticationService: AuthenticationService)
    {
        super(formBuilder.group({
            secret: ['', Validators.required]
        }), 'secret', authenticationService);
    }

    protected override onSubmit()
    {
        const secret: string | undefined = this.secret;
        if (!secret || !this.loginData)
        {
            return;
        }

        this.authenticationService.verifyCredential(secret, this.loginData).subscribe({error: ((error: any) => {
            // TODO add error codes
        })});
    }

    protected get secret(): string | undefined
    {
        return this.form.get('secret')?.value;
    }
}
