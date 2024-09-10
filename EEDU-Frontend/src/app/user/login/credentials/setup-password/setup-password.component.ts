import {Component, Input} from '@angular/core';
import {MatDialogClose, MatDialogContent, MatDialogTitle} from "@angular/material/dialog";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {LoginData} from "../../authentication/login-data/login-data";

@Component({
  selector: 'app-setup-password',
  standalone: true,
    imports: [
        MatDialogContent,
        MatButton,
        MatDialogClose,
        MatFormField,
        MatIcon,
        MatIconButton,
        MatInput,
        MatLabel,
        MatSuffix,
        ReactiveFormsModule,
        MatDialogTitle
    ],
  templateUrl: './setup-password.component.html',
  styleUrl: './setup-password.component.scss'
})
export class SetupPasswordComponent {


    @Input() private _loginData?: LoginData;
    private readonly _formGroup: FormGroup;
    private _showPassword: boolean = false;
    private _showRepeatPassword: boolean = false;


    constructor(formBuilder: FormBuilder) {
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

    get loginData(): LoginData | undefined
    {
        return this._loginData;
    }
}
