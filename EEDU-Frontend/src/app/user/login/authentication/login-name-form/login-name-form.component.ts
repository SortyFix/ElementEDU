import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output, signal, WritableSignal} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatDialogClose} from "@angular/material/dialog";
import {MatError, MatFormField, MatHint, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {LoginRequest} from "../login-data/login-request";
import {NgIf} from "@angular/common";

@Component({
    selector: 'app-login-name-form', standalone: true, imports: [
        MatButton,
        MatHint,
        MatError,
        MatCheckbox,
        MatDialogClose,
        MatFormField,
        MatInput,
        MatLabel,
        ReactiveFormsModule,
        FormsModule,
        NgIf
    ], templateUrl: './login-name-form.component.html', styleUrl: './login-name-form.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginNameFormComponent
{
    @Output() readonly submit: EventEmitter<LoginRequest> = new EventEmitter<LoginRequest>();
    @Input() errorSignal: WritableSignal<any> = signal('');
    loginName?: string;
    rememberMe: boolean = false;

    onSubmit()
    {
        if (!this.loginName)
        {
            return;
        }
        const request = new LoginRequest(this.loginName, this.rememberMe);
        this.submit.emit(request);
    }
}
