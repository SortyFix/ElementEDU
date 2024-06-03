import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {NgOptimizedImage} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {MatDialogModule} from "@angular/material/dialog";
import {provideHttpClient, withFetch} from "@angular/common/http";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatError, MatFormField, MatHint, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {CdkCopyToClipboard} from "@angular/cdk/clipboard";
import {MatTooltip} from "@angular/material/tooltip";
import {DialogCloseButtonComponent} from "./dialog/dialog-close-button/dialog-close-button.component";
import {DialogHeaderComponent} from "./dialog/dialog-header/dialog-header.component";
import {RequestLoginComponent} from "./user/login/authentication/auth-modal/request/request-login.component";
import {PasswordComponent} from "./user/login/authentication/auth-modal/password/password.component";
import {AuthModalComponent} from "./user/login/authentication/auth-modal/auth-modal.component";

@NgModule({
    declarations: [AppComponent, RequestLoginComponent, PasswordComponent, AuthModalComponent],
    imports: [
        MatLabel,
        FormsModule,
        MatHint,
        MatLabel,
        MatDialogModule,
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        MatIconModule,
        MatButtonModule,
        NgOptimizedImage,
        FormsModule,
        MatSlideToggle,
        MatCheckbox,
        MatFormField,
        MatError,
        MatInput,
        MatSuffix,
        CdkCopyToClipboard,
        MatTooltip,
        DialogCloseButtonComponent,
        DialogHeaderComponent,
    ],
    providers: [
        provideHttpClient(withFetch())
    ],
    bootstrap: [AppComponent],
    exports: [
        RequestLoginComponent,
        PasswordComponent
    ]
})
export class AppModule {

}
