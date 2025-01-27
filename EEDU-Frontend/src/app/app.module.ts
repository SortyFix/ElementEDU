import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {NgOptimizedImage} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatDialogModule} from "@angular/material/dialog";
import {provideHttpClient, withFetch} from "@angular/common/http";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatError, MatFormField, MatFormFieldModule, MatHint, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput, MatInputModule} from "@angular/material/input";
import {CdkCopyToClipboard} from "@angular/cdk/clipboard";
import {MatTooltip} from "@angular/material/tooltip";
import {LoadComponent} from "./load/load.component";
import { DashboardComponent } from './dashboard/dashboard.component';
import { AbstractComponent } from './abstract/abstract.component';
import {MatSidenavModule} from "@angular/material/sidenav";
import {Authentication} from "./user/authentication/authentication.component";
import {CdkDrag, CdkDropList} from "@angular/cdk/drag-drop";
import {MatNativeDateModule} from "@angular/material/core";
import {MatDatepickerModule} from "@angular/material/datepicker";

@NgModule({
    declarations: [
        AppComponent,
        DashboardComponent,
        AbstractComponent
    ],
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
        BrowserModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatDatepickerModule,
        MatNativeDateModule,
        FormsModule,
        MatSlideToggle,
        MatCheckbox,
        MatFormField,
        MatError,
        MatInput,
        MatSuffix,
        CdkCopyToClipboard,
        MatTooltip,
        LoadComponent,
        NgOptimizedImage,
        MatSidenavModule,
        Authentication,
        CdkDropList,
        CdkDrag
    ],
    providers: [
        provideHttpClient(withFetch())
    ],
    bootstrap: [AppComponent],
    exports: [
        AbstractComponent
    ]
})
export class AppModule {

}
