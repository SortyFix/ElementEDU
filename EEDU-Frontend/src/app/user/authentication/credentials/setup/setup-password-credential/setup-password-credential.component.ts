import {Component} from '@angular/core';
import {LoginData} from "../../../login-data/login-data";
import {AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {AuthenticationService} from "../../../authentication.service";
import {AbstractCredentialForm} from "../../abstract-credential-form";

@Component({
  selector: 'app-setup-password-credential',
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
  templateUrl: './setup-password-credential.component.html',
  styleUrl: './setup-password-credential.component.scss'
})
export class SetupPasswordCredentialComponent extends AbstractCredentialForm {

    private _showPassword: boolean = false;
    private _showRepeatPassword: boolean = false;

    constructor(formBuilder: FormBuilder, authenticationService: AuthenticationService) {
        super(formBuilder.group({
            password: ['', [Validators.required]],
            repeatPassword: ['', [Validators.required]]
        }), authenticationService);
    }

    protected onShowPassword(event: MouseEvent)
    {
        event.stopPropagation()
        this._showPassword = !this._showPassword;
    }

    protected get showPassword(): boolean
    {
        return this._showPassword;
    }

    protected onShowRepeatPassword(event: MouseEvent)
    {
        event.stopPropagation()
        this._showRepeatPassword = !this._showRepeatPassword;
    }

    protected get showRepeatPassword(): boolean
    {
        return this._showRepeatPassword;
    }

    protected onSubmit(): void {
        console.log("test")
    }
}
