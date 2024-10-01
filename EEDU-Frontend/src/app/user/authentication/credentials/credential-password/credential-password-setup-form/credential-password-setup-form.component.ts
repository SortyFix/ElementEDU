import { Component } from '@angular/core';
import {AbstractCredentialForm} from "../../abstract-credential-form";
import {FormBuilder, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthenticationService} from "../../../authentication.service";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";

@Component({
  selector: 'app-credential-password-setup-form',
  standalone: true,
    imports: [
        MatFormField,
        MatIcon,
        MatLabel,
        MatInput,
        ReactiveFormsModule,
        MatIconButton,
        MatDialogClose,
        MatButton
    ],
  templateUrl: './credential-password-setup-form.component.html',
  styleUrl: './credential-password-setup-form.component.scss'
})
export class CredentialPasswordSetupFormComponent extends AbstractCredentialForm {

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
    }
}
