import {Component, input, InputSignal, OnInit} from '@angular/core';
import {LoginData} from "../../../login-data/login-data";
import {CredentialMethod} from "../../../login-data/credential-method";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatProgressBar} from "@angular/material/progress-bar";
import {ReactiveFormsModule} from "@angular/forms";
import {AuthenticationService} from "../../../authentication.service";
import {NgIf, NgOptimizedImage} from "@angular/common";
import {MatCardAvatar} from "@angular/material/card";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {QRCodeModule} from "angularx-qrcode";
import {TotpData} from "./totp-data";

@Component({
    selector: 'app-setup-totp-credential', standalone: true, imports: [
        MatButton,
        MatDialogClose,
        MatProgressBar,
        ReactiveFormsModule,
        NgOptimizedImage,
        NgIf,
        MatCardAvatar,
        MatFormField,
        MatInput,
        MatLabel,
        QRCodeModule,
    ], templateUrl: './setup-totp-credential.component.html', styleUrl: './setup-totp-credential.component.scss'
})
export class SetupTotpCredentialComponent implements OnInit
{

    public readonly loginData: InputSignal<LoginData | undefined> = input<LoginData>();
    private _credentialData?: TotpData;

    constructor(private authService: AuthenticationService) {}

    public ngOnInit(): void
    {
        const loginData: LoginData | undefined = this.loginData();
        if (!loginData)
        {
            throw new Error("Cannot show form without login data.")
        }

        this.authService.setupCredential(CredentialMethod.TOTP, loginData, undefined).subscribe({
            next: ((value: string) =>
            {
                this._credentialData = new TotpData(
                    JSON.parse(value).loginName,
                    JSON.parse(value).secret,
                    JSON.parse(value).algorithm,
                    JSON.parse(value).digits,
                    JSON.parse(value).period
                );
            })
        });
    }

    protected get credentialData(): TotpData
    {
        if(!this._credentialData)
        {
            throw new Error("Credential data is not fetched yet.")
        }
        return this._credentialData;
    }

    protected get doneLoading(): boolean
    {
        return !!this._credentialData;
    }
}
