import {Component, HostListener, OnInit} from '@angular/core';
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
import {LoginRequest} from "../authentication/auth-modal/request/login-request";
import {AuthorizeService} from "../authentication/authorize.service";
import {animate, style, transition, trigger} from "@angular/animations";
import {MatProgressBar} from "@angular/material/progress-bar";

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
    ], templateUrl: './test.component.html', styleUrl: './test.component.scss', animations: [
        trigger('loginNameAnimation', [
            transition(':leave', [
                animate('0.3s', style({transform: 'translateX(-100%)'}))
            ])
        ]), trigger('passwordAnimation', [
            transition(':enter', [
                style({transform: 'translateX(0%)'}), animate('0.3s', style({transform: 'translateX(-100%)'}))
            ]), transition(':leave', [
                animate('0.3s', style({transform: 'translateX(-100%)'}))
            ])
        ])
    ]
})
export class TestComponent implements OnInit
{
    animationState: string = "loginForm";

    loginRequest?: LoginRequest;
    errorMessage?: string;
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
        this.showLoadingThing = true;
        if (data == false)
        {
            this.animationState = "slideLoginNameForm"
            this.loginRequest = undefined;
            return;
        }

        if (data instanceof LoginRequest)
        {
            this.showLoadingThing = false;
            this.loginRequest = data;
            this.animationState = "passwordForm";
            /*            this.authorizeService.request(data).subscribe({
                            next: () => this.loginRequest = data,
                            error: (error) =>
                            {
                                this.errorMessage = this.getErrorMessage(error)
                            }
                        }).add(() => this.showLoadingThing = true)*/
            return;
        }
    }

    getErrorMessage(error: any): string
    {
        return "An error occurred"
    }
}
