import {Component} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {AbstractCodeCredential} from "../abstract-code-credential";

@Component({
    selector: 'app-credential-sms-form',
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
    templateUrl: './credential-sms-form.component.html',
    styleUrl: './credential-sms-form.component.scss'
})
export class CredentialSmsFormComponent extends AbstractCodeCredential<{smsCode: string}>
{
    protected override onSubmit(): void
    {
        const code: string | undefined = this.code;
        if (!code)
        {

            return;
        }
        this.emit({smsCode: code});
    }
}
