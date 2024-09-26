import {Component} from '@angular/core';
import {LoginData} from "../../../login-data/login-data";
import {AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {AuthenticationService} from "../../../authentication.service";

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
export class SetupPasswordCredentialComponent {

    private readonly _formGroup: FormGroup;
    private _showPassword: boolean = false;
    private _showRepeatPassword: boolean = false;


    constructor(formBuilder: FormBuilder, private _authService: AuthenticationService) {
        this._formGroup = formBuilder.group({
            password: ['', [Validators.required, this.passwordsMustMatch]],
            repeatPassword: ['', [Validators.required, this.passwordsMustMatch]]
        });
    }

    private passwordsMustMatch(control: AbstractControl): { [key: string]: boolean } | undefined {
        const password = control.get('password')?.value;
        const repeatPassword = control.get('repeatPassword')?.value;

        if (password !== repeatPassword) {
            return { passwordsMismatch: true };
        }

        return undefined;
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


    get formGroup(): FormGroup
    {
        return this._formGroup;
    }

    protected get loginData(): LoginData | undefined {
        return this._authService.loginData;
    }
}
