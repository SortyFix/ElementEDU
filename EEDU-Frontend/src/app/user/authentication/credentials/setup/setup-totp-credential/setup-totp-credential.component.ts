import {Component, Input, OnInit} from '@angular/core';
import {LoginData} from "../../../login-data/login-data";
import {CredentialMethod} from "../../../login-data/credential-method";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatProgressBar} from "@angular/material/progress-bar";
import {ReactiveFormsModule} from "@angular/forms";
import {AuthenticationService} from "../../../authentication.service";
import {NgIf, NgOptimizedImage} from "@angular/common";

@Component({
  selector: 'app-setup-totp-credential',
  standalone: true,
    imports: [
        MatButton,
        MatDialogClose,
        MatProgressBar,
        ReactiveFormsModule,
        NgOptimizedImage,
        NgIf
    ],
  templateUrl: './setup-totp-credential.component.html',
  styleUrl: './setup-totp-credential.component.scss'
})
export class SetupTotpCredentialComponent implements OnInit {

    @Input() _loginData?: LoginData;
    private _base64?: string;

    constructor(private authService: AuthenticationService) {}

    public ngOnInit(): void
    {
        if(!this.loginData)
        {
            throw new Error("Cannot show form without login data.")
        }

        this.authService.setupCredential(CredentialMethod.TOTP, this.loginData, undefined).subscribe({
            next: ((value: string | undefined) => this._base64 = `data:image/png;base64, ${value}`)
        });
    }

    protected get base64(): string | undefined
    {
        return this._base64;
    }

    protected get loginData(): LoginData | undefined
    {
        return this._loginData;
    }

}
