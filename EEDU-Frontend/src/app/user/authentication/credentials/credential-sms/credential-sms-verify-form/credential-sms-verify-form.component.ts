import {Component} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {AbstractCredentialVerifyCode} from "../../abstract-credential-verify-code";

@Component({
    selector: 'app-credential-sms-verify-form',
    standalone: true,
    imports: [
        FormsModule,
        MatButton,
        MatFormField,
        MatInput,
        MatLabel,
        ReactiveFormsModule
    ],
    templateUrl: './credential-sms-verify-form.component.html',
    styleUrl: './credential-sms-verify-form.component.scss'
})
export class CredentialSmsVerifyFormComponent extends AbstractCredentialVerifyCode {}
