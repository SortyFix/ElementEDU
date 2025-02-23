import {Component} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatError, MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {AbstractCredentialVerifyCode} from "../../abstract-credential-verify-code";
import {FooterButtonsComponent} from "../../../common/footer-buttons/footer-buttons.component";
import {FormTitleComponent} from "../../../common/form-title/form-title.component";

@Component({
    selector: 'app-credential-totp-verify-form',
    standalone: true,
    imports: [
        FormsModule,
        MatFormField,
        MatInput,
        MatLabel,
        ReactiveFormsModule,
        MatError,
        FooterButtonsComponent,
        FormTitleComponent,
    ],
    templateUrl: './credential-totp-verify-form.component.html',
    styleUrl: './credential-totp-verify-form.component.scss'
})
export class CredentialTotpVerifyFormComponent extends AbstractCredentialVerifyCode {

    protected override errorMessage(status: number): string {
        if (status == 401) {
            return "This code is incorrect."
        }
        return super.errorMessage(status);
    }
}
