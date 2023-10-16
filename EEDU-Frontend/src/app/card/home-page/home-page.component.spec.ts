import {ComponentFixture, TestBed} from '@angular/core/testing';

import {HomePageComponent} from './home-page.component';
import {HeadBarComponent} from "../../head-bar/head-bar.component";
import {DashboardComponent} from "../dashboard/dashboard.component";
import {MatCardModule} from "@angular/material/card";
import {MatGridListModule} from "@angular/material/grid-list";
import {CardComponent} from "../card.component";
import {KlausurenCardComponent} from "../klausuren-card/klausuren-card.component";
import {HausaufgabenCardComponent} from "../hausaufgaben-card/hausaufgaben-card.component";
import {ChatCardComponent} from "../chat-card/chat-card.component";
import {MatIcon, MatIconModule} from "@angular/material/icon";
import {MatMenuModule} from "@angular/material/menu";

describe('HomePageComponent', () => {
    let component: HomePageComponent;
    let fixture: ComponentFixture<HomePageComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [
                HomePageComponent,
                HeadBarComponent,
                DashboardComponent,
                CardComponent,
                KlausurenCardComponent,
                HausaufgabenCardComponent,
                ChatCardComponent,
            ],
            imports: [
                MatGridListModule,
                MatCardModule,
                MatIconModule,
                MatMenuModule,
            ]
        });
        fixture = TestBed.createComponent(HomePageComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
