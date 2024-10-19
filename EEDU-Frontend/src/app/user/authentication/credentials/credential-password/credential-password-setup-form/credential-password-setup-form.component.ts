import {Component} from '@angular/core';
import {AbstractCredentialForm} from "../../abstract-credential-form";
import {
    AbstractControl,
    FormBuilder,
    ReactiveFormsModule,
    ValidationErrors,
    ValidatorFn,
    Validators
} from "@angular/forms";
import {AuthenticationService} from "../../../authentication.service";
import {MatError, MatFormField, MatHint, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {CredentialMethod} from "../../../login-data/credential-method";
import {NgForOf, NgIf} from "@angular/common";
import {FooterButtonsComponent} from "../../../common/footer-buttons/footer-buttons.component";
import {FormTitleComponent} from "../../../common/form-title/form-title.component";
import {MatList, MatListItem} from "@angular/material/list";

@Component({
    selector: 'app-credential-password-setup-form',
    standalone: true,
    imports: [MatFormField, MatIcon, MatLabel, MatHint, MatInput, ReactiveFormsModule, MatIconButton, MatDialogClose, MatButton, MatError, MatSuffix, NgIf, FooterButtonsComponent, FormTitleComponent, MatList, MatListItem, NgForOf,],
    templateUrl: './credential-password-setup-form.component.html',
    styleUrl: './credential-password-setup-form.component.scss'
})
export class CredentialPasswordSetupFormComponent extends AbstractCredentialForm {

    private _passwordVisibility: { password: boolean, repeatPassword: boolean } = {
        password: false, repeatPassword: false
    };

    constructor(formBuilder: FormBuilder, authenticationService: AuthenticationService) {
        super(formBuilder.group({
            password: ['', [Validators.required, passwordValidator()]], repeatPassword: ['', [Validators.required]]
        }), authenticationService);

        this.registerField('password');
        this.registerField('repeatPassword');
    }


    protected toggleVisibility(field: 'password' | 'repeatPassword'): void {
        this._passwordVisibility[field] = !this._passwordVisibility[field];
    }

    protected isVisible(field: 'password' | 'repeatPassword'): boolean {
        return this._passwordVisibility[field];
    }

    protected inputType(field: 'password' | 'repeatPassword'): 'text' | 'password' {
        return this.isVisible(field) ? 'text' : 'password';
    }

    protected icon(field: 'password' | 'repeatPassword'): 'visibility' | 'visibility_off' {
        return this.isVisible(field) ? 'visibility' : 'visibility_off';
    }

    protected onSubmit(): void {
        const password: string = this.form.get('password')?.value;
        const repeatPassword: string = this.form.get('repeatPassword')?.value;

        if (password !== repeatPassword) {
            this.error = {field: 'repeatPassword', serverError: 'The passwords do not match.'}
            return;
        }

        this.authenticationService.setupCredential(CredentialMethod.PASSWORD, password).subscribe({
            next: (): void => { this.authenticationService.enableCredential(password).subscribe(); },
            error: (error: any): void => this.exceptionHandler('password').error(error)
        });
    }


    protected override errorMessage(status: number): string {
        if (status === 406) {
            return "This password does not match the requirements."
        }

        return super.errorMessage(status);
    }
}

export function passwordValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        const password: string = control.value;
        if (!password) {
            return null;
        }

        const passwordRequirements = {
            length: password.length >= 6,
            lowercase: /[a-z]/.test(password),
            uppercase: /[A-Z]/.test(password),
            number: /[0-9]/.test(password),
            special: /[!"#$%&'()*+,\-./:;<=>?@\[\\\]^_`{|}~]/.test(password)
        };

        const errorMessages: Record<string, string> = {
            length: 'Password must be at least 6 characters long',
            lowercase: 'Password must contain at least one lowercase letter',
            uppercase: 'Password must contain at least one uppercase letter',
            number: 'Password must contain at least one number',
            special: 'Password must contain at least one special character'
        };

        const errors: ValidationErrors = {};

        Object.keys(passwordRequirements).forEach((key: string): void => {
            if (!passwordRequirements[key as keyof typeof passwordRequirements]) {
                errors[key] = errorMessages[key];
            }
        });

        if (Object.keys(errors).length === 0) {
            return null;
        }

        return errors;
    };
}
