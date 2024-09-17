import {Component} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {AbstractCredentialCodeForm} from "../abstract-credential-code-form";

@Component({
    selector: 'app-credential-email-form', standalone: true, imports: [
        FormsModule,
        MatButton,
        MatDialogClose,
        MatFormField,
        MatIcon,
        MatIconButton,
        MatInput,
        MatLabel,
        MatSuffix,
        ReactiveFormsModule
    ], templateUrl: './credential-email-form.component.html', styleUrl: './credential-email-form.component.scss'
})
export class CredentialEmailFormComponent extends AbstractCredentialCodeForm
{
    protected override get emptyMessage(): string {
        return "Please enter the code we've sent to your E-Mail."
    }
}
