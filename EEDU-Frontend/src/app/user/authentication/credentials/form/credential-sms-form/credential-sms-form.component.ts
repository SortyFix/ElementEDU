import {Component} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {AbstractCredentialCode} from "../../abstract-credential-code";
import {AbstractCredentialCodeForm} from "../abstract-credential-code-form";

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
export class CredentialSmsFormComponent extends AbstractCredentialCodeForm {}
