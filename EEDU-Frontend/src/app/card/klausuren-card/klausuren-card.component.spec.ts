import {ComponentFixture, TestBed} from '@angular/core/testing';

import {KlausurenCardComponent} from './klausuren-card.component';
import {MatCardModule} from "@angular/material/card";
import {MatMenuModule} from "@angular/material/menu";
import {MatIconModule} from "@angular/material/icon";

describe('KlausurenCardComponent', () => {
    let component: KlausurenCardComponent;
    let fixture: ComponentFixture<KlausurenCardComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [
                KlausurenCardComponent
            ],
            imports: [
                MatCardModule,
                MatMenuModule,
                MatIconModule,
            ]

        });
        fixture = TestBed.createComponent(KlausurenCardComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
