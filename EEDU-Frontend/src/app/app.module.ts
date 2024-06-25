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
import {Authentication} from "./user/login/authentication/authentication.component";
import {LoadComponent} from "./load/load.component";
import { DashboardComponent } from './dashboard/dashboard.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import {MatSidenavModule} from "@angular/material/sidenav";
import {CoursesComponent} from "./pages/courses/courses.component";
import {TimetableComponent} from "./pages/timetable/timetable.component";
import {NewsComponent} from "./pages/news/news.component";
import {ChatComponent} from "./pages/chat/chat.component";
import {SettingsComponent} from "./pages/settings/settings.component";
import {SchoolComponent} from "./pages/school/school.component";

@NgModule({
    declarations: [
        AppComponent,
        DashboardComponent,
        SidebarComponent
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
        FormsModule,
        MatSlideToggle,
        MatCheckbox,
        MatFormField,
        MatError,
        MatInput,
        MatSuffix,
        CdkCopyToClipboard,
        MatTooltip,
        Authentication,
        LoadComponent,
        NgOptimizedImage,
        MatSidenavModule,
        CoursesComponent,
        TimetableComponent,
        NewsComponent,
        ChatComponent,
        SettingsComponent,
        SchoolComponent
    ],
    providers: [
        provideHttpClient(withFetch())
    ],
    bootstrap: [AppComponent],
    exports: [
        SidebarComponent
    ]
})
export class AppModule {

}
