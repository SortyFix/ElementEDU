import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output, signal, WritableSignal} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatDialogClose} from "@angular/material/dialog";
import {MatError, MatFormField, MatHint, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {LoginRequest} from "../login-data/login-request";
import {NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";

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
        NgIf,
        MatIcon,
        MatSuffix
    ], templateUrl: './login-name-form.component.html', styleUrl: './login-name-form.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginNameFormComponent
{
    @Output() readonly submit: EventEmitter<LoginRequest> = new EventEmitter<LoginRequest>();
    @Input() errorSignal: WritableSignal<any> = signal('');
    private readonly _loginForm: FormGroup;

    constructor(formBuilder: FormBuilder)
    {
        this._loginForm = formBuilder.group({
            loginName: ['', Validators.required],
            rememberMe: [false]
        });
    }

    protected onSubmit(): void
    {
        const loginName = this.loginForm.get('loginName')?.value;
        const rememberMe = this.loginForm.get('rememberMe')?.value;
        if (!loginName) {
            this.errorSignal.set('Login name is required');
            return;
        }
        const request = new LoginRequest(loginName, rememberMe);
        this.submit.emit(request);
    }

    protected get loginForm(): FormGroup
    {
        return this._loginForm;
    }
}
