import {Component, EventEmitter, Input, Output} from '@angular/core';
import {LoginRequest} from "./login-request";

@Component({
    selector: 'app-request-login',
    templateUrl: './request-login.component.html',
    styleUrls: ['./request-login.component.scss']
})
export class RequestLoginComponent
{
    @Output() submit: EventEmitter<LoginRequest> = new EventEmitter<LoginRequest>();
    @Input() error?: string;
    loginName?: string;
    rememberMe: boolean = false;

    onSubmit()
    {
        if(!this.loginName)
        {
            return;
        }
        this.submit.emit(new LoginRequest(this.loginName, this.rememberMe))
    }
}
