import {Component} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {AbstractCredential} from "../abstract-credential";
import {AbstractSecretCredential} from "../abstract-secret-credential";

@Component({
    selector: 'app-credential-totp-form',
    standalone: true,
    imports: [
        FormsModule,
        MatButton,
        MatDialogClose,
        MatFormField,
        MatInput,
        MatLabel,
        ReactiveFormsModule
    ],
    templateUrl: './credential-totp-form.component.html',
    styleUrl: './credential-totp-form.component.scss'
})
export class CredentialTotpFormComponent extends AbstractSecretCredential
{
    protected override emptyMessage(): string {
        return "Please enter the code your Authenticator App shows.";
    }

}
