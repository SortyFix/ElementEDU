import {Component} from '@angular/core';
import {LoginRequest} from "../verification/login-request";

@Component({
    selector: 'app-auth-modal',
    templateUrl: './auth-modal.component.html',
    styleUrl: './auth-modal.component.scss',
})
export class AuthModalComponent
{
    password?: string;
    loginRequest?: LoginRequest;

    onLoginSubmit(loginRequest: LoginRequest)
    {
        this.loginRequest = loginRequest;
    }

    onPasswordSubmit(password: string)
    {
        this.password = password;
        console.log("loginRequest:", this.loginRequest);
        console.log("password:", this.password);
    }
}
