import {Component} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {AbstractCodeCredential} from "../abstract-code-credential";

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
export class CredentialTotpFormComponent extends AbstractCodeCredential
{
    protected override onSubmit(): void
    {
        if (!this._code)
        {
            return;
        }
        this.emit({totpCode: this._code});
    }
}
