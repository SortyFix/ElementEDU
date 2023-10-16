import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeadBarComponent } from './head-bar.component';
import {AppRoutingModule} from "../app-routing.module";

describe('HeadBarComponent', () => {
    let component: HeadBarComponent;
    let fixture: ComponentFixture<HeadBarComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [ HeadBarComponent ],
            imports: [
                AppRoutingModule
            ]
        })
            .compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(HeadBarComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
