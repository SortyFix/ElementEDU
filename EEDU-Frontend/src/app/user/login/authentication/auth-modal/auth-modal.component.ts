import {Component} from '@angular/core';
import {LoginRequest} from "./request/login-request";
import {AuthorizeService} from "../authorize.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatDialog} from "@angular/material/dialog";

@Component({
    selector: 'app-auth-modal', templateUrl: './auth-modal.component.html', styleUrl: './auth-modal.component.scss',
})
export class AuthModalComponent
{
    isAuthorized?: boolean = false;
    loginRequest?: LoginRequest;
    error?: string;

    constructor(private auth: AuthorizeService, private snackBar: MatSnackBar, private dialog: MatDialog)
    {
        this.auth.isAuthorized().then((value) => {
            this.isAuthorized = value;
        })
    }

    onLoginSubmit(loginRequest: any)
    {
        if (!(loginRequest instanceof LoginRequest))
        {
            return;
        }

        this.auth.request(loginRequest).subscribe({
            next: () =>
            {
                this.error = ""; // reset
                this.loginRequest = loginRequest
            }, error: () =>
            {
                this.error = "An error occurred"
            }
        })
    }

    onPasswordSubmit(password: any)
    {
        if (typeof password != 'string')
        {
            return;
        }
        this.auth.verifyPassword(password).subscribe({
            next: () =>
            {
                this.error = "";
                this.snackBar.open("You successfully logged in as " + this.loginRequest?.loginName, "Got it", {
                    horizontalPosition: "center", verticalPosition: "top", duration: 2000
                })
                this.dialog.closeAll()
            }, error: () =>
            {
                this.error = "Password is not correct"
            }
        })
    }
}
