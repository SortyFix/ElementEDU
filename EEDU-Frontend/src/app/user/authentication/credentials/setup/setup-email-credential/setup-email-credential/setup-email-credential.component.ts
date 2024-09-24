import {Component, Input} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {LoginData} from "../../../../login-data/login-data";

@Component({
  selector: 'app-setup-email-credential',
  standalone: true,
    imports: [
        MatButton,
        MatDialogClose,
        MatFormField,
        MatInput,
        MatLabel,
        ReactiveFormsModule
    ],
  templateUrl: './setup-email-credential.component.html',
  styleUrl: './setup-email-credential.component.scss'
})
export class SetupEmailCredentialComponent {

    @Input() private _loginData?: LoginData;
    private readonly _formGroup: FormGroup;

    constructor(formBuilder: FormBuilder) {
        this._formGroup = formBuilder.group({
            email: ['', [Validators.required, Validators.email]],
        });
    }

    protected get loginData(): LoginData | undefined
    {
        return this._loginData;
    }

    protected get formGroup(): FormGroup
    {
        return this._formGroup;
    }
}
