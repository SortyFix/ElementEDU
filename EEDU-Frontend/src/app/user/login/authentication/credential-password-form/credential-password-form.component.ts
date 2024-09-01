import {Component, EventEmitter, Input, Output, signal, WritableSignal} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatAnchor, MatButton, MatIconButton} from "@angular/material/button";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {LoginData} from "../login-data/login-data";

@Component({
    selector: 'app-credential-password-form', standalone: true, imports: [
        FormsModule,
        MatButton,
        MatCheckbox,
        MatDialogClose,
        MatInput,
        MatLabel,
        ReactiveFormsModule,
        MatIcon,
        MatIconButton,
        MatSuffix,
        MatAnchor,
        MatFormField
    ], templateUrl: './credential-password-form.component.html', styleUrl: './credential-password-form.component.scss'
})
export class CredentialPasswordFormComponent
{
    @Output() private readonly _submit = new EventEmitter<{ password: string } | boolean>();
    @Input() errorSignal: WritableSignal<any> = signal('');
    @Input() _loginData?: LoginData;
    private _showPassword: boolean = false;
    protected password?: string;

    protected onShowPassword(event: MouseEvent)
    {
        event.stopPropagation()
        this._showPassword = !this._showPassword;
    }

    protected get showPassword(): boolean
    {
        return this._showPassword;
    }

    protected onSubmit()
    {
        if(!this.password)
        {
            return;
        }
        this._submit.emit({password: this.password});
    }

    protected onCancel()
    {
        this._submit.emit(false)
    }

    protected get loginData(): LoginData | undefined
    {
        return this._loginData;
    }
}
