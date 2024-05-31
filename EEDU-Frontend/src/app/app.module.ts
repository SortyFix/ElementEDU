import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatCardModule} from '@angular/material/card';
import {MatMenuModule} from '@angular/material/menu';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {NgOptimizedImage} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {PasswordComponent} from './user/login/password/password.component';
import {RequestLoginComponent} from "./user/login/request/request-login.component";
import {MatDialogModule} from "@angular/material/dialog";
import {provideHttpClient, withFetch} from "@angular/common/http";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatError, MatFormField, MatHint, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {AuthModalComponent} from "./user/login/auth-modal/auth-modal.component";

@NgModule({
    declarations: [AppComponent, RequestLoginComponent, PasswordComponent, AuthModalComponent],
    imports: [MatHint, MatLabel, MatDialogModule, BrowserModule, AppRoutingModule, BrowserAnimationsModule, MatGridListModule, MatCardModule, MatMenuModule, MatIconModule, MatButtonModule, NgOptimizedImage, FormsModule, MatSlideToggle, MatCheckbox, MatFormField, MatError, MatInput, MatSuffix,],
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
