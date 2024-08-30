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
    @Output() readonly submit = new EventEmitter<any>();
    @Input() errorSignal: WritableSignal<any> = signal('');
    @Input() _loginData?: LoginData;
    protected password?: string;
    protected showPassword: boolean = false;

    protected onShowPassword(event: MouseEvent)
    {
        event.stopPropagation()
        this.showPassword = !this.showPassword;
    }

    protected onCancel()
    {
        this.submit.emit(false)
    }

    protected onSubmit()
    {
        if(!this.password)
        {
            return;
        }
        this.submit.emit(this.password);
    }

    get loginData(): LoginData | undefined
    {
        return this._loginData;
    }
}
