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
import {
    SetupEmailCredentialComponent
} from "./credentials/setup/setup-email-credential/setup-email-credential/setup-email-credential.component";
import {
    SetupPasswordCredentialComponent
} from "./credentials/setup/setup-password-credential/setup-password-credential.component";
import {
    SetupSmsCredentialComponent
} from "./credentials/setup/setup-sms-credential/setup-sms-credential/setup-sms-credential.component";
import {SetupTotpCredentialComponent} from "./credentials/setup/setup-totp-credential/setup-totp-credential.component";

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
        SetupEmailCredentialComponent,
        SetupPasswordCredentialComponent,
        SetupSmsCredentialComponent,
        SetupTotpCredentialComponent,
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
    private _mobile: boolean = false;
    private _loadingAnimation: boolean = false;
    private _statusCode?: number;

    /**
     * Constructor to initialize the LoginComponent.
     *
     * @param authService The service used for login-related operations such as login and password verification.
     */
    constructor(protected authService: AuthenticationService)
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

    protected get statusCode(): number | undefined
    {
        return this._statusCode;
    }

    protected readonly LoginData = LoginData;
}
