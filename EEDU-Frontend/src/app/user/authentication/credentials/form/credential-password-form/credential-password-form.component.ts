import {Component} from '@angular/core';
import {ReactiveFormsModule} from "@angular/forms";
import {MatError, MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {AbstractCredentialCodeForm} from "../abstract-credential-code-form";


@Component({
    selector: 'app-credential-password-form', standalone: true, imports: [
        MatFormField,
        MatIcon,
        MatInput,
        MatIconButton,
        MatButton,
        MatDialogClose,
        ReactiveFormsModule,
        MatLabel,
        MatSuffix,
        MatIconButton,
        MatError
    ], templateUrl: './credential-password-form.component.html', styleUrl: './credential-password-form.component.scss'
})
export class CredentialPasswordFormComponent extends AbstractCredentialCodeForm
{
    private _showPassword: boolean = false;

    protected onShowPassword(event: MouseEvent)
    {
        event.stopPropagation()
        this._showPassword = !this._showPassword;
    }

    protected get showPassword(): boolean
    {
        return this._showPassword;
    }
}
