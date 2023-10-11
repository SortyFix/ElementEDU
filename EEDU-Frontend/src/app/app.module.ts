import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeadBarComponent} from './head-bar/head-bar.component';
import {DashComponent} from './dash/dash.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DashboardComponent } from './dashboard/dashboard.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { CardComponent } from './card/card.component';
import { KlausurenCardComponent } from './klausuren-card/klausuren-card.component';
import { ChatCardComponent } from './chat-card/chat-card.component';
import { HausaufgabenCardComponent } from './hausaufgaben-card/hausaufgaben-card.component';

@NgModule({
    declarations: [
        AppComponent,
        HeadBarComponent,
        DashComponent,
        DashboardComponent,
        CardComponent,
        KlausurenCardComponent,
        ChatCardComponent,
        HausaufgabenCardComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        MatGridListModule,
        MatCardModule,
        MatMenuModule,
        MatIconModule,
        MatButtonModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
