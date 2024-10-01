import {Component, HostListener, OnInit} from '@angular/core';
import {MatCard, MatCardContent, MatCardFooter, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {NgComponentOutlet, NgIf, NgOptimizedImage} from "@angular/common";
import {LoginNameFormComponent} from "./login-name-form/login-name-form.component";
import {MatProgressBar} from "@angular/material/progress-bar";
import {SelectCredentialComponent} from "./select-credential-form/select-credential.component";
import {animate, style, transition, trigger} from "@angular/animations";
import {credentialDisplayName, CredentialMethod} from "./login-data/credential-method";
import {MatAnchor} from "@angular/material/button";
import {MatDivider} from "@angular/material/divider";
import {MatError, MatLabel} from "@angular/material/form-field";
import {AuthenticationService} from "./authentication.service";
import {
    CredentialTotpVerifyFormComponent
} from "./credentials/credential-totp/credential-totp-verify-form/credential-totp-verify-form.component";
import {
    CredentialEmailVerifyFormComponent
} from "./credentials/credential-email/credential-email-verify-form/credential-email-verify-form.component";
import {
    CredentialPasswordVerifyFormComponent
} from "./credentials/credential-password/credential-password-verify-form/credential-password-verify-form.component";
import {
    CredentialEmailSetupFormComponent
} from "./credentials/credential-email/credential-email-setup-form/credential-email-setup-form/credential-email-setup-form.component";
import {
    CredentialPasswordSetupFormComponent
} from "./credentials/credential-password/credential-password-setup-form/credential-password-setup-form.component";
import {
    CredentialSmsSetupFormComponent
} from "./credentials/credential-sms/credential-sms-setup-form/credential-sms-setup-form/credential-sms-setup-form.component";
import {
    CredentialTotpSetupFormComponent
} from "./credentials/credential-totp/credential-totp-setup-form/credential-totp-setup-form.component";
import {
    CredentialSmsVerifyFormComponent
} from "./credentials/credential-sms/credential-sms-verify-form/credential-sms-verify-form.component";
import {MatListOption} from "@angular/material/list";

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
        CredentialTotpVerifyFormComponent,
        CredentialEmailVerifyFormComponent,
        CredentialPasswordVerifyFormComponent,
        CredentialEmailSetupFormComponent,
        CredentialPasswordSetupFormComponent,
        CredentialSmsSetupFormComponent,
        CredentialTotpSetupFormComponent,
        NgComponentOutlet,
        MatListOption
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
export class Authentication implements OnInit {

    private _mobile: boolean = false;

    private readonly _footerLinks: {name: string, link: string, icon: string}[] = [
        {name: "Privacy", link: "#", icon: "privacy_tip"},
        {name: "Legal Notice", link: "#", icon: "gavel"},
        {name: "Contact", link: "#", icon: "contact_mail"},
        {name: "Terms of Service", link: "#", icon: "description"},
        {name: "Help", link: "#", icon: "help"},
    ];


    protected get footerLinks(): { name: string; link: string; icon: string }[] {
        return this._footerLinks;
    }

    /**
     * Constructor to initialize the LoginComponent.
     *
     * @param authService The service used for login-related operations such as login and password verification.
     */
    constructor(protected authService: AuthenticationService) {}

    /**
     * Determines and returns the current component based on the login state and credential method.
     * If no login data is present, the {@link LoginNameFormComponent} is returned.
     * If the login data exists but no credential method is selected, the {@link SelectCredentialComponent} is returned.
     * Depending on whether the credential setup process is in progress or not, it returns the appropriate setup or verification component.
     *
     * @return The component to be displayed based on the user's login state and credential method.
     */
    protected get currentComponent(): any {
        if (!this.authService.loginData) {
            return LoginNameFormComponent;
        }

        if (!this.authService.loginData.credential) {
            return SelectCredentialComponent;
        }

        const credential: CredentialMethod = this.authService.loginData.credential;

        if (this.authService.loginData.setupCredential) {
            return this.setupComponent(credential);
        }

        return this.verifyComponent(credential);
    }

    /**
     * Lifecycle hook that is called after data-bound properties of a directive are initialized.
     * Initializes the screen size check.
     */
    public ngOnInit(): void {
        this.checkScreenSize()
    }

    /**
     * Listens for window resize events to check and update the screen size.
     */
    @HostListener("window:resize")
    public onResize(): void {
        this.checkScreenSize()
    }

    /**
     * Checks the screen size and sets the mobile property based on the width.
     */
    private checkScreenSize(): void {
        this._mobile = window.innerWidth <= 600;
    }

    /**
     * Returns whether this is currently in mobile mode or not
     * @protected
     */
    protected get mobile(): boolean {
        return this._mobile;
    }

    /**
     * Returns the appropriate credential setup form component based on the provided credential method.
     *
     * @param credentialMethod The method of credential (e.g., PASSWORD, EMAIL, SMS, TOTP).
     * @return The credential setup form component corresponding to the specified credential method.
     */
    private setupComponent(credentialMethod: CredentialMethod): any {
        switch (credentialMethod) {
            case CredentialMethod.PASSWORD:
                return CredentialPasswordSetupFormComponent;
            case CredentialMethod.EMAIL:
                return CredentialEmailSetupFormComponent;
            case CredentialMethod.SMS:
                return CredentialSmsSetupFormComponent;
            case CredentialMethod.TOTP:
                return CredentialTotpSetupFormComponent;
        }
    }

    /**
     * Returns the appropriate credential verification form component based on the provided credential method.
     *
     * @param credentialMethod The method of credential (e.g., PASSWORD, EMAIL, SMS, TOTP).
     * @return The credential verification form component corresponding to the specified credential method.
     */
    private verifyComponent(credentialMethod: CredentialMethod): any {
        switch (credentialMethod) {
            case CredentialMethod.PASSWORD:
                return CredentialPasswordVerifyFormComponent;
            case CredentialMethod.EMAIL:
                return CredentialEmailVerifyFormComponent;
            case CredentialMethod.SMS:
                return CredentialSmsVerifyFormComponent;
            case CredentialMethod.TOTP:
                return CredentialTotpVerifyFormComponent;
        }
    }
}
