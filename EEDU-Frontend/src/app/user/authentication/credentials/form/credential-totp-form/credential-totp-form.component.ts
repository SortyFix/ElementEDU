import {Component} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {AbstractCredentialCodeForm} from "../abstract-credential-code-form";

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
export class CredentialTotpFormComponent extends AbstractCredentialCodeForm {}
