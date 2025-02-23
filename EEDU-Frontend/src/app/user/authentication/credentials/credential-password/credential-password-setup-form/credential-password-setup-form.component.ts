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
import {MatError, MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {MatIconButton} from "@angular/material/button";
import {CredentialMethod} from "../../../login-data/credential-method";
import {NgIf} from "@angular/common";
import {FooterButtonsComponent} from "../../../common/footer-buttons/footer-buttons.component";
import {FormTitleComponent} from "../../../common/form-title/form-title.component";

type inputFieldType = 'password' | 'repeatPassword';

@Component({
    selector: 'app-credential-password-setup-form',
    standalone: true,
    imports: [MatFormField, MatIcon, MatLabel, MatInput, ReactiveFormsModule, MatIconButton, MatError, MatSuffix, NgIf, FooterButtonsComponent, FormTitleComponent,],
    templateUrl: './credential-password-setup-form.component.html',
    styleUrl: './credential-password-setup-form.component.scss'
})
export class CredentialPasswordSetupFormComponent extends AbstractCredentialForm {

    private _passwordVisibility: { password: boolean, repeatPassword: boolean } = {
        password: false, repeatPassword: false
    };

    constructor(formBuilder: FormBuilder, authenticationService: AuthenticationService) {
        super(formBuilder.group({
            password: ['', [Validators.required, passwordValidator()]],
            repeatPassword: ['', [Validators.required, repeatValidator()]]
        }), authenticationService);

        this.registerField('password');
        this.registerField('repeatPassword');
    }

    /**
     * Toggles the visibility of a password field (either {@code password} or {@code repeatPassword}).
     *
     * This method will change the visibility state of the specified field, toggling it between
     * visible and hidden each time it is called.
     *
     * @param field the field to toggle the visibility for. Can be either {@code password} or {@code repeatPassword}.
     */
    protected toggleVisibility(field: inputFieldType): void {
        this._passwordVisibility[field] = !this._passwordVisibility[field];
    }

    /**
     * Checks whether a password field is visible or hidden.
     *
     * This method returns {@code true} if the specified field is currently visible,
     * and {@code false} if it is hidden.
     *
     * @param field the field to check the visibility for. Can be either {@code password} or {@code repeatPassword}.
     * @returns {@code true} if the field is visible, otherwise {@code false}.
     */
    protected isVisible(field: inputFieldType): boolean {
        return this._passwordVisibility[field];
    }

    /**
     * Returns the input type for a password field based on its visibility.
     *
     * This method determines whether the specified field should be rendered as a text input
     * or a password input. If the field is visible, it returns {@code text};
     * otherwise, it returns {@code password}.
     *
     * @param field the field to check the input type for. Can be either {@code password} or {@code repeatPassword}.
     * @returns the input type: either {@code text} if the field is visible, or {@code password} if hidden.
     */
    protected inputType(field: inputFieldType): 'text' | 'password' {
        return this.isVisible(field) ? 'text' : 'password';
    }

    /**
     * Returns the appropriate icon for the password field based on its visibility.
     *
     * This method provides an icon that reflects the visibility of the specified field.
     * If the field is visible, the icon will be {@code visibility}. If the field
     * is hidden, the icon will be {@code visibility_off}.
     *
     * @param field the field to get the icon for. Can be either {@code password} or {@code repeatPassword}.
     * @returns the icon: either {@code visibility} if the field is visible, or {@code visibility_off} if hidden.
     */
    protected icon(field: inputFieldType): 'visibility' | 'visibility_off' {
        return this.isVisible(field) ? 'visibility' : 'visibility_off';
    }

    protected override onSubmit(): void {
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
        } else if (status === 400) {
            return "You cannot use the same password again!"
        }

        return super.errorMessage(status);
    }
}

/**
 * A custom validator function for password validation.
 * The password must meet the following criteria:
 * - At least 6 characters long
 * - Contains at least one lowercase letter
 * - Contains at least one uppercase letter
 * - Contains at least one number
 * - Contains at least one special character
 *
 * @returns a {@link ValidatorFn} that checks the password validity.
 */
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

/**
 * A custom validator function for repeating the password.
 * The password in the {@code repeatPassword} field must match the password in the {@code password} field.
 *
 * @returns a {@link ValidatorFn} that checks if the passwords match.
 */
export function repeatValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        const currentInput: string = control.value;
        const password: string = control.parent?.get('password')?.value;

        if (password === currentInput) {
            return null
        }

        return {mismatch: 'The password does not match with the above'};
    };
}
