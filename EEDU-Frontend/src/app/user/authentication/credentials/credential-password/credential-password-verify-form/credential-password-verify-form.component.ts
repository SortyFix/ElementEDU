import {Component} from '@angular/core';
import {ReactiveFormsModule} from "@angular/forms";
import {MatError, MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {MatIconButton} from "@angular/material/button";
import {AbstractCredentialVerifyCode} from "../../abstract-credential-verify-code";
import {FormTitleComponent} from "../../../common/form-title/form-title.component";
import {FooterButtonsComponent} from "../../../common/footer-buttons/footer-buttons.component";

@Component({
    selector: 'app-credential-password-verify-form',
    standalone: true,
    imports: [
        MatFormField,
        MatIcon,
        MatInput,
        MatIconButton,
        ReactiveFormsModule,
        MatLabel,
        MatSuffix,
        MatIconButton,
        MatError,
        FormTitleComponent,
        FooterButtonsComponent,
    ],
    templateUrl: './credential-password-verify-form.component.html',
    styleUrl: './credential-password-verify-form.component.scss'
})
export class CredentialPasswordVerifyFormComponent extends AbstractCredentialVerifyCode {
    private _showPassword: boolean = false;

    protected get showPassword(): boolean {
        return this._showPassword;
    }

    protected onShowPassword(): void {
        this._showPassword = !this._showPassword;
    }

    protected override errorMessage(error: number): string {

        if (error == 403) {
            return 'Either the username or the password is incorrect.'
        }
        return super.errorMessage(error);
    }
}
