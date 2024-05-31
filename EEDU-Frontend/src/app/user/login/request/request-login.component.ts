import {Component, EventEmitter, Output} from '@angular/core';
import {AuthorizeService} from "../verification/authorize.service";
import {animate, style, transition, trigger} from "@angular/animations";
import {LoginRequest} from "../verification/login-request";

export const dialogTransition = trigger('dialogTransition', [transition(':enter', [style({
    opacity: 0,
    transform: 'scale(0.8)'
}), // Initial state
    animate('300ms cubic-bezier(0.25, 0.8, 0.25, 1)', style({opacity: 1, transform: 'scale(1)'})) // Transition to fully visible and scale to 1
]), transition(':leave', [animate('300ms cubic-bezier(0.25, 0.8, 0.25, 1)', style({
    opacity: 0,
    transform: 'scale(0.8)'
})) // Transition to invisible and scale to 0.8
])]);

@Component({
    selector: 'app-request-login', templateUrl: './request-login.component.html', styleUrls: ['./request-login.component.scss']
})
export class RequestLoginComponent {
    @Output() submit= new EventEmitter<LoginRequest>();
    keepLoggedIn: boolean = false;
    loginName?: string;
    error?: string;

    interactedLoginName: boolean = false;

    constructor(private authService: AuthorizeService) {
    }

    onInteractLoginName() {
        this.interactedLoginName = true;
    }

    onSubmit() {
        if (!this.loginName) {
            return;
        }
        this.submit.emit(new LoginRequest(this.loginName, this.keepLoggedIn));

        /*        const request = new LoginRequest(this.loginName, this.keepLoggedIn)
                this.authService.request(request).subscribe({
                    next: () => {
                        this.error = "";
                    },
                    error: error => {
                        this.error = this.getErrorMessage(error.status);
                    }
                });*/
    }

    getErrorMessage(statusCode: number): string {
        switch (statusCode) {
            case 423: // fall through
            case 401:
                this.loginName = "";
                return "Access denied.";
            default:
                return "An unknown error occurred.";
        }
    }
}
