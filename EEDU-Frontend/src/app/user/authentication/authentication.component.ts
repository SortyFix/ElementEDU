import {Component, EventEmitter, HostListener, OnInit, Output} from '@angular/core';
import {MatCard, MatCardContent, MatCardFooter, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {NgIf, NgOptimizedImage} from "@angular/common";
import {LoginNameFormComponent} from "./login-name-form/login-name-form.component";
import {MatProgressBar} from "@angular/material/progress-bar";
import {finalize, MonoTypeOperatorFunction} from "rxjs";
import {SelectCredentialComponent} from "./select-credential-form/select-credential.component";
import {animate, style, transition, trigger} from "@angular/animations";
import {LoginData} from "./login-data/login-data";
import {LoginRequest} from "./login-data/login-request";
import {CredentialMethod} from "./login-data/credential-method";
import {MatAnchor} from "@angular/material/button";
import {MatDivider} from "@angular/material/divider";
import {MatError, MatLabel} from "@angular/material/form-field";
import {AuthenticationService} from "./authentication.service";
import {CredentialTotpFormComponent} from "./credentials/form/credential-totp-form/credential-totp-form.component";
import {CredentialEmailFormComponent} from "./credentials/form/credential-email-form/credential-email-form.component";
import {
    CredentialPasswordFormComponent
} from "./credentials/form/credential-password-form/credential-password-form.component";

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
        MatCardFooter,
        NgOptimizedImage,
        NgIf,
        SelectCredentialComponent,
        MatAnchor,
        MatDivider,
        MatError,
        MatLabel,
        CredentialTotpFormComponent,
        CredentialEmailFormComponent,
        CredentialPasswordFormComponent,
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
    protected readonly CredentialMethod: typeof CredentialMethod = CredentialMethod;
    private _loginData?: LoginData;
    private _mobile: boolean = false;
    private _loadingAnimation: boolean = false;
    private _statusCode?: number;

    /**
     * Constructor to initialize the LoginComponent.
     *
     * @param loginService The service used for login-related operations such as login and password verification.
     */
    constructor(private loginService: AuthenticationService)
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
    @HostListener("window:resize")
    public onResize(): void
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
    protected onSubmit(data: any): void
    {
        this._statusCode = undefined;
        if (typeof data == 'boolean')
        {
            this._loginData = undefined;
            return;
        }

        if (data instanceof LoginRequest)
        {
            this.requestCredentials(data);
            return;
        }

        if (!data || typeof data != "object" || !this.loginData)
        {
            return;
        }

        if ('method' in data && typeof data.method === 'string')
        {
            this.loginService.selectCredential(data.method, this.loginData).pipe(this.finalizeLoading()).subscribe();
        }
        else if (this.loginData.credential && 'secret' in data && typeof data.secret === 'string')
        {
            this.loginService.verifyCredential(data.secret, this.loginData).pipe(this.finalizeLoading()).subscribe({
                next: () => this.submit.emit(), error: error => this.statusCode = error
            });
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
        this.loginService.request(data).pipe(this.finalizeLoading()).subscribe({
            next: (loginData) => this._loginData = loginData,
            error: (error) => this.statusCode = error

        });
    }

    private set statusCode(error: any)
    {
        if(typeof error == 'object' && 'status' in error && typeof error.status == 'number')
        {
            this._statusCode = error.status;
        }
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

    protected get statusCode(): number | undefined
    {
        return this._statusCode;
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
        if (typeof error == 'object' && 'status' in error && typeof error.status == 'number')
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
