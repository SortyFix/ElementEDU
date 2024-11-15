import {Component} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatError, MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {AbstractCredentialVerifyCode} from "../../abstract-credential-verify-code";
import {FooterButtonsComponent} from "../../../common/footer-buttons/footer-buttons.component";
import {FormTitleComponent} from "../../../common/form-title/form-title.component";

@Component({
    selector: 'app-credential-email-verify-form', standalone: true,
    imports: [
        FormsModule,
        MatButton,
        MatDialogClose,
        MatError,
        MatFormField,
        MatIcon,
        MatIconButton,
        MatInput,
        MatLabel,
        MatSuffix,
        ReactiveFormsModule,
        FooterButtonsComponent,
        FormTitleComponent,
    ], templateUrl: './credential-email-verify-form.component.html', styleUrl: './credential-email-verify-form.component.scss'
})
export class CredentialEmailVerifyFormComponent extends AbstractCredentialVerifyCode {}
