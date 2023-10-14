import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeadBarComponent} from './card/head-bar/head-bar.component';
import {DashComponent} from './dash/dash.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DashboardComponent } from './card/dashboard/dashboard.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { CardComponent } from './card/card.component';
import { KlausurenCardComponent } from './card/klausuren-card/klausuren-card.component';
import { ChatCardComponent } from './card/chat-card/chat-card.component';
import { HausaufgabenCardComponent } from './card/hausaufgaben-card/hausaufgaben-card.component';
import { LoginPageComponent } from './user/login-page/login-page.component';
import { HomePageComponent } from './card/home-page/home-page.component';
import { HttpClientModule } from "@angular/common/http";

@NgModule({
    declarations: [
        AppComponent,
        HeadBarComponent,
        DashComponent,
        DashboardComponent,
        CardComponent,
        KlausurenCardComponent,
        ChatCardComponent,
        HausaufgabenCardComponent,
        LoginPageComponent,
        HomePageComponent
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
        MatButtonModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
