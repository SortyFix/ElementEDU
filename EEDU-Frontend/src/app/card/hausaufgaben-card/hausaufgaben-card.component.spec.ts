import {ComponentFixture, TestBed} from '@angular/core/testing';

import {HausaufgabenCardComponent} from './hausaufgaben-card.component';
import {MatCard, MatCardContent, MatCardHeader, MatCardModule, MatCardTitle} from "@angular/material/card";
import {MatIcon, MatIconModule} from "@angular/material/icon";
import {MatMenu, MatMenuModule} from "@angular/material/menu";

describe('HausaufgabenCardComponent', () => {
    let component: HausaufgabenCardComponent;
    let fixture: ComponentFixture<HausaufgabenCardComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                MatCardModule,
                MatMenuModule,
                MatIconModule,
            ],
            declarations: [
                HausaufgabenCardComponent
            ]
        });
        fixture = TestBed.createComponent(HausaufgabenCardComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
