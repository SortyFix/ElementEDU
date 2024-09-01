import {Component, EventEmitter, HostListener, OnInit, Output, signal, WritableSignal} from '@angular/core';
import {MatCard, MatCardContent, MatCardFooter, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {NgIf, NgOptimizedImage} from "@angular/common";
import {LoginNameFormComponent} from "./login-name-form/login-name-form.component";
import {CredentialPasswordFormComponent} from "./credential-password-form/credential-password-form.component";
import {MatProgressBar} from "@angular/material/progress-bar";
import {UserService} from "../../user.service";
import {finalize, MonoTypeOperatorFunction} from "rxjs";
import {SelectCredentialComponent} from "./select-credential-form/select-credential.component";
import {animate, style, transition, trigger} from "@angular/animations";
import {LoginData} from "./login-data/login-data";
import {LoginRequest} from "./login-data/login-request";
import {CredentialMethod} from "./login-data/credential-method";
import {MatAnchor} from "@angular/material/button";
import {MatDivider} from "@angular/material/divider";
import {CredentialEmailFormComponent} from "./credential-email-form/credential-email-form.component";
import {CredentialTotpFormComponent} from "./credential-totp-form/credential-totp-form.component";
import {MatError} from "@angular/material/form-field";

@Component({
    selector: 'app-authentication', standalone: true, imports: [
        MatCard,
        MatProgressBar,
        MatCardHeader,
        MatCardTitle,
        MatIcon,
        MatCardContent,
        MatGridList,
        MatGridTile,
        LoginNameFormComponent,
        CredentialPasswordFormComponent,
        MatCardFooter,
        NgOptimizedImage,
        NgIf,
        SelectCredentialComponent,
        MatAnchor,
        MatDivider,
        MatError,
        CredentialTotpFormComponent,
        CredentialEmailFormComponent,
    ], templateUrl: './authentication.component.html', styleUrl: './authentication.component.scss', animations: [
        trigger('loginNameAnimation', [
            transition(':leave', [
                animate('0.3s', style({transform: 'translateX(-100%)'}))
            ])
        ]), trigger('passwordAnimation', [
            transition(':enter', [
                style({transform: 'translateX(0%)'}), animate('0.3s', style({transform: 'translateX(-100%)'}))
            ]), transition(':leave', [
                animate('0.3s', style({transform: 'translateX(-100%)'}))
            ])
        ])
    ]
})
export class Authentication implements OnInit
{
    @Output() submit: EventEmitter<void> = new EventEmitter<void>();
    protected errorSignal: WritableSignal<any> = signal('');
    protected readonly CredentialMethod: typeof CredentialMethod = CredentialMethod;
    private _loginData?: LoginData;
    private _mobile: boolean = false;
    private _loadingAnimation: boolean = false;

    /**
     * Constructor to initialize the LoginComponent.
     *
     * @param userService The service used for user-related operations such as login and password verification.
     */
    constructor(private userService: UserService)
    {
    }

    /**
     * Lifecycle hook that is called after data-bound properties of a directive are initialized.
     * Initializes the screen size check.
     */
    public ngOnInit(): void
    {
        this.checkScreenSize()
    }

    /**
     * Listens for window resize events to check and update the screen size.
     */
    @HostListener("window:resize") public onResize(): void
    {
        this.checkScreenSize()
    }

    /**
     * Checks the screen size and sets the mobile property based on the width.
     */
    private checkScreenSize(): void
    {
        this._mobile = window.innerWidth <= 600;
    }

    /**
     * Handles the submission of login data. Determines if the provided data is a password or login request and processes it accordingly.
     *
     * @param data The data submitted for login, either a password string or a LoginRequest object.
     */
    onSubmit(data: any): void
    {

        if (typeof data == 'boolean' && !data)
        {
            this._loginData = undefined;
            return;
        }

        if (data && typeof data == "object" && this.loginData)
        {
            if ('method' in data && typeof data.method === 'string')
            {
                this.userService.selectCredential(data.method, this.loginData).pipe(this.finalizeLoading()).subscribe();
            }
            else if ('password' in data && typeof data.password === 'string')
            {
                this.verifyPassword(data.password);
            }
        }
        else if (data instanceof LoginRequest)
        {
            this.requestCredentials(data);
        }
    }

    /**
     * Sends a login request to the server.
     *
     * This method sends an HTTP POST request to the backend server to verify the existence of the given user.
     * If the user exists, it sets the login data to the given credentials and proceeds accordingly.
     *
     * @param data The login request data containing user credentials.
     * @private
     */
    private requestCredentials(data: LoginRequest): void
    {
        this.userService.request(data).pipe(this.finalizeLoading()).subscribe({
            next: (loginData) => this._loginData = loginData,
            error: (error) => {
                this.errorSignal.set(this.getErrorMessage(error))
            }
        });
    }

    /**
     * Verifies the provided password with the server.
     *
     * This method sends an HTTP POST request to the backend server to verify the provided password.
     * If the password is correct, it emits the submit event. If an error occurs, it sets the error signal with an appropriate error message.
     *
     * @param password The password to be verified.
     * @private
     */
    private verifyPassword(password: string): void
    {
        if (!this.loginData)
        {
            return;
        }

        this.userService.verifyPassword(password, this.loginData).pipe(this.finalizeLoading()).subscribe({
            next: () => this.submit.emit(), error: error => this.errorSignal.set(this.getErrorMessage(error))
        });
    }

    private finalizeLoading(): MonoTypeOperatorFunction<any>
    {
        return finalize((): void => {this._loadingAnimation = false;});
    }


    protected get mobile(): boolean
    {
        return this._mobile;
    }

    protected get loadingAnimation(): boolean
    {
        return this._loadingAnimation;
    }


    protected get loginData(): LoginData | undefined
    {
        return this._loginData;
    }

    /**
     * Retrieves an appropriate error message based on the provided error.
     *
     * This method takes an error object as input and returns a user-friendly error message. The method
     * analyzes the error type and details to provide a specific and helpful error message to the user.
     *
     * @param error The error object containing information about the error that occurred.
     * @return A string representing the specific error message.
     * @private
     */
    private getErrorMessage(error: any): string
    {
        if(typeof error == 'object' && 'status' in error && typeof error.status == 'number')
        {
            switch (error.status)
            {
                case 401:
                    return "The user has not been found."
            }
        }
        return "An error occurred"
    }
}
