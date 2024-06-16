import {Component, HostListener, OnInit, signal, WritableSignal} from '@angular/core';
import {
    MatCard,
    MatCardActions,
    MatCardContent,
    MatCardFooter,
    MatCardHeader,
    MatCardSubtitle,
    MatCardTitle
} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {ReactiveFormsModule} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {MatDialogClose} from "@angular/material/dialog";
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {MatDivider} from "@angular/material/divider";
import {NgIf, NgOptimizedImage} from "@angular/common";
import {MatCheckbox} from "@angular/material/checkbox";
import {LoginNameFormComponent} from "./login-name-form/login-name-form.component";
import {PasswordFormComponent} from "./password-form/password-form.component";
import {MatProgressBar} from "@angular/material/progress-bar";
import {LoginRequest} from "./login-name-form/login-request";
import {AuthorizeService} from "./authorize.service";

@Component({
    selector: 'app-test', standalone: true, imports: [
        MatCard,
        MatCardTitle,
        MatCardSubtitle,
        MatCardContent,
        MatCardActions,
        MatButton,
        MatFormField,
        MatInput,
        MatLabel,
        ReactiveFormsModule,
        MatCardHeader,
        MatIcon,
        MatDialogClose,
        MatGridList,
        MatGridTile,
        MatDivider,
        NgOptimizedImage,
        MatCheckbox,
        LoginNameFormComponent,
        PasswordFormComponent,
        NgIf,
        MatCardFooter,
        MatProgressBar,
    ], templateUrl: './authentication.component.html', styleUrl: './authentication.component.scss'
})
export class Authentication implements OnInit
{
    loginRequest?: LoginRequest;
    errorSignal: WritableSignal<any> = signal('');
    mobile: boolean = false;
    showLoadingThing: boolean = false;

    constructor(private authorizeService: AuthorizeService) {}

    ngOnInit(): void
    {
        this.checkScreenSize()
    }

    @HostListener("window:resize", ["$event"]) onResize(event: any)
    {
        this.checkScreenSize()
    }

    checkScreenSize()
    {
        this.mobile = window.innerWidth <= 600;
    }

    onSubmit(data: any)
    {
        if (data == false)
        {
            this.loginRequest = undefined;
            return;
        }

        this.showLoadingThing = true;
        if (data instanceof LoginRequest)
        {
            this.showLoadingThing = false;
            this.authorizeService.request(data).subscribe({
                next: () => this.loginRequest = data, error: (error) =>
                {
                    this.errorSignal.set(this.getErrorMessage(error))
                }
            }).add(() => this.showLoadingThing = false)
            return;
        }
    }

    getErrorMessage(error: any): string
    {
        return "An error occurred"
    }
}
