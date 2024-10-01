import {Component} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {AbstractCredentialVerifyCode} from "../../abstract-credential-verify-code";

@Component({
    selector: 'app-credential-email-verify-form', standalone: true, imports: [
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
    ], templateUrl: './credential-email-verify-form.component.html', styleUrl: './credential-email-verify-form.component.scss'
})
export class CredentialEmailVerifyFormComponent extends AbstractCredentialVerifyCode {}
