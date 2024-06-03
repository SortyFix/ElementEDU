import {Component, HostListener, OnInit} from '@angular/core';
import {
    MatCard,
    MatCardActions,
    MatCardContent, MatCardFooter,
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
import {animate, state, style, transition, trigger} from "@angular/animations";

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
    ], templateUrl: './test.component.html', styleUrl: './test.component.scss', animations: [
        trigger('formSwitch', [
            state('loginNameForm', style({
                transform: 'translateX(0%)'
            })),
            state('passwordForm', style({
                transform: 'translateX(-100%)'
            })),
            transition('loginNameForm => passwordForm', [
                animate('0.5s ease-in-out')
            ]),
            transition('passwordForm => loginNameForm', [
                animate('0.5s ease-in-out')
            ])
        ])
    ]
})
export class TestComponent implements OnInit
{

    loginRequest?: LoginRequest;
    errorMessage?: string;
    mobile: boolean = false;

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

        if (data instanceof LoginRequest)
        {
            this.loginRequest = data;
            return;
        }

        /*        this.authorizeService.request(event).subscribe({
                    next: () => this.loginRequest = event,
                    error: (error) => this.errorMessage = this.getErrorMessage(error)
                })*/
    }

    getErrorMessage(error: any): string
    {
        return "An error occurred"
    }
}
