import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SettingsComponent} from './settings.component';
import {DashboardComponent} from "../../card/dashboard/dashboard.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

describe('SettingsComponent', () => {
    let component: SettingsComponent;
    let fixture: ComponentFixture<SettingsComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                DashboardComponent,
            ],
            declarations: [
                SettingsComponent
            ],
            imports: [
                BrowserAnimationsModule,
            ]
        });
        fixture = TestBed.createComponent(SettingsComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
