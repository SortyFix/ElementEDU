import {Component, Input, OnInit} from '@angular/core';
import {LoginData} from "../../authentication/login-data/login-data";
import {MatFormField} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatProgressBar} from "@angular/material/progress-bar";
import {LoginService} from "../../login.service";
import {CredentialMethod} from "../../authentication/login-data/credential-method";

@Component({
  selector: 'app-setup-totp',
  standalone: true,
    imports: [
        MatFormField,
        MatInput,
        ReactiveFormsModule,
        MatButton,
        MatDialogClose,
        MatProgressBar
    ],
  templateUrl: './setup-totp.component.html',
  styleUrl: './setup-totp.component.scss'
})
export class SetupTOTPComponent implements OnInit {

    @Input() private _loginData?: LoginData;
    private _base64?: string;

    constructor(private loginService: LoginService) {}

    public ngOnInit(): void
    {
        this.loginService.setupCredential(CredentialMethod.TOTP, undefined).subscribe({
            next: ((value: string | undefined) => this._base64 = value)
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
