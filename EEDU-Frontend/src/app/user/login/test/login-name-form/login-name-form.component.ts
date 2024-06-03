import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatDialogClose} from "@angular/material/dialog";
import {MatError, MatFormField, MatHint, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {LoginRequest} from "../../authentication/auth-modal/request/login-request";

@Component({
    selector: 'app-login-name-form', standalone: true, imports: [
        MatButton, MatHint, MatError, MatCheckbox, MatDialogClose, MatFormField, MatInput, MatLabel, ReactiveFormsModule, FormsModule
    ], templateUrl: './login-name-form.component.html', styleUrl: './login-name-form.component.scss'
})
export class LoginNameFormComponent
{
    @Output() submit: EventEmitter<LoginRequest> = new EventEmitter<LoginRequest>();
    @Input() errorMessage?: string;
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
