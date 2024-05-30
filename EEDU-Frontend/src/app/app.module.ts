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
import {HttpClientModule, provideHttpClient, withFetch} from "@angular/common/http";
import {NgOptimizedImage} from "@angular/common";
import {LoginComponent} from './user/login/login.component';
import {WidgetComponent} from './widget/widget.component';
import {MatDialogModule} from "@angular/material/dialog";

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        WidgetComponent,
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        MatGridListModule,
        MatCardModule,
        MatMenuModule,
        MatIconModule,
        HttpClientModule,
        MatButtonModule,
        NgOptimizedImage,
    ],
    providers: [
        provideHttpClient(withFetch()),
    ],
    bootstrap: [AppComponent],
})
export class AppModule {
}
