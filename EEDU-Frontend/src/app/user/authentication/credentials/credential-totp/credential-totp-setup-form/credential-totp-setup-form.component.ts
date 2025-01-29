import {Component, OnInit} from '@angular/core';
import {AbstractCredentialSetupCode} from "../../abstract-credential-setup-code";
import {TotpData} from "./totp-data";
import {FormBuilder, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {AuthenticationService} from "../../../authentication.service";
import {LoginData} from "../../../login-data/login-data";
import {CredentialMethod} from "../../../login-data/credential-method";
import {MatError, MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatProgressBar} from "@angular/material/progress-bar";
import {NgIf} from "@angular/common";
import {FooterButtonsComponent} from "../../../common/footer-buttons/footer-buttons.component";
import {QRCodeComponent} from "angularx-qrcode";

@Component({
  selector: 'app-credential-totp-setup-form',
  standalone: true,
    imports: [
        FormsModule,
        MatError,
        MatFormField,
        MatInput,
        MatLabel,
        MatProgressBar,
        NgIf,
        ReactiveFormsModule,
        FooterButtonsComponent,
        QRCodeComponent,
    ],
  templateUrl: './credential-totp-setup-form.component.html',
  styleUrl: './credential-totp-setup-form.component.scss'
})
export class CredentialTotpSetupFormComponent extends AbstractCredentialSetupCode implements OnInit
{
    private _credentialData?: TotpData;

    constructor(formBuilder: FormBuilder, private readonly authService: AuthenticationService)
    {
        super(formBuilder, authService);
    }

    public ngOnInit(): void
    {
        const loginData: LoginData | undefined = this.loginData;
        if (!loginData)
        {
            throw new Error("Cannot show form without login data.")
        }

        this.authService.setupCredential(CredentialMethod.TOTP).subscribe({
            next: ((value: string): void =>
            {
                const jsonData: any = JSON.parse(value);
                this._credentialData = new TotpData(
                    jsonData.loginName,
                    jsonData.secret,
                    jsonData.algorithm,
                    jsonData.digits,
                    jsonData.period,
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

    protected override errorMessage(status: number): string {
        if(status == 401)
        {
            return "This code is incorrect."
        }
        return super.errorMessage(status);
    }
}
