import {ChangeDetectionStrategy, Component} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatError, MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormBuilder, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {AbstractLoginForm} from "../abstract-login-form";
import {LoginRequest} from "../login-data/login-request";
import {AuthenticationService} from "../authentication.service";
import {FormTitleComponent} from "../common/form-title/form-title.component";

@Component({
    selector: 'app-login-name-form',
    standalone: true,
    imports: [
        MatButton,
        MatError,
        MatCheckbox,
        MatFormField,
        MatInput,
        MatLabel,
        ReactiveFormsModule,
        FormsModule,
        FormTitleComponent
    ],
    templateUrl: './login-name-form.component.html',
    styleUrl: './login-name-form.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginNameFormComponent extends AbstractLoginForm {
    constructor(formBuilder: FormBuilder, authenticationService: AuthenticationService) {
        super(formBuilder.group({
            loginName: [null, Validators.required], rememberMe: [false]
        }), authenticationService);

        this.registerField('loginName')
    }

    protected override onSubmit(): void {

        if (this.form.invalid) {
            return;
        }

        const loginName: string = (this.form.get('loginName')?.value as string).trim();
        const rememberMe: boolean = this.form.get('rememberMe')?.value;

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
