import {ChangeDetectionStrategy, Component} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatDialogClose} from "@angular/material/dialog";
import {MatError, MatFormField, MatHint, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormBuilder, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {AbstractLoginForm} from "../abstract-login-form";
import {LoginRequest} from "../login-data/login-request";
import {AuthenticationService} from "../authentication.service";

@Component({
    selector: 'app-login-name-form',
    standalone: true,
    imports: [
        MatButton,
        MatHint,
        MatError,
        MatCheckbox,
        MatDialogClose,
        MatFormField,
        MatInput,
        MatLabel,
        ReactiveFormsModule,
        FormsModule,
        NgIf,
        MatIcon,
        MatSuffix
    ],
    templateUrl: './login-name-form.component.html',
    styleUrl: './login-name-form.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginNameFormComponent extends AbstractLoginForm {
    constructor(formBuilder: FormBuilder, authenticationService: AuthenticationService) {
        super(formBuilder.group({
            loginName: ['', Validators.required], rememberMe: [false]
        }), authenticationService);

        this.registerField('loginName')
    }

    protected override onSubmit(): void {
        const loginName = this.form.get('loginName')?.value;
        const rememberMe = this.form.get('rememberMe')?.value;

        const loginRequest: LoginRequest = new LoginRequest(loginName, rememberMe);
        this.authenticationService.requestAuthorization(loginRequest).subscribe(this.exceptionHandler('loginName'));
    }

    protected override errorMessage(error: number): string {
        if (error == 401) {
            return `The user ${this.form.get('loginName')?.value} was not found.`
        }
        return super.errorMessage(error);
    }
}
