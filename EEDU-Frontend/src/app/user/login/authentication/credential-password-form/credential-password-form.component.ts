import {Component} from '@angular/core';
import {AbstractCredential} from "../abstract-credential";
import {FormBuilder, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";


@Component({
    selector: 'app-credential-password-form', standalone: true, imports: [
        MatFormField,
        MatIcon,
        MatInput,
        MatIconButton,
        MatButton,
        MatDialogClose,
        ReactiveFormsModule,
        MatLabel
    ], templateUrl: './credential-password-form.component.html', styleUrl: './credential-password-form.component.scss'
})
export class CredentialPasswordFormComponent extends AbstractCredential<{ password: string }>
{
    private _showPassword: boolean = false;
    constructor(formBuilder: FormBuilder)
    {
        super(formBuilder.group({password: ['', Validators.required]}));
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

    protected override onSubmit(): void
    {
        const password: string | undefined = this.form.get('password')?.value;
        if (!password)
        {
            this.errorSignal.set("Password cannot be empty.");
            return;
        }
        this.emit({password: password})
    }
}
