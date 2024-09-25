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
export class LoginNameFormComponent extends AbstractLoginForm<LoginRequest> {
    constructor(formBuilder: FormBuilder, authenticationService: AuthenticationService) {
        super(formBuilder.group({
            loginName: ['', Validators.required], rememberMe: [false]
        }), 'loginName', authenticationService);
    }

    protected override onSubmit(): void {
        const loginName = this.form.get('loginName')?.value;
        const rememberMe = this.form.get('rememberMe')?.value;

        console.log("test")

        this.authenticationService.requestAuthorization(new LoginRequest(loginName, rememberMe)).subscribe({
            error: (error: any) => {
                //TODO
            }
        });
    }
}
