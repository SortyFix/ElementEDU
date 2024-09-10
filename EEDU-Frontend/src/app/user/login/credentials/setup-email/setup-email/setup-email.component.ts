import {Component, Input} from '@angular/core';
import {LoginData} from "../../../authentication/login-data/login-data";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";

@Component({
  selector: 'app-setup-email',
  standalone: true,
    imports: [
        MatButton,
        MatDialogClose,
        MatFormField,
        MatIcon,
        MatIconButton,
        MatInput,
        MatLabel,
        MatSuffix,
        ReactiveFormsModule
    ],
  templateUrl: './setup-email.component.html',
  styleUrl: './setup-email.component.scss'
})
export class SetupEmailComponent {
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
