import {ComponentFixture, TestBed} from '@angular/core/testing';

import {EventDataDialogComponent} from './event-data-dialog.component';

describe('UpdateEventComponent', () => {
    let component: EventDataDialogComponent;
    let fixture: ComponentFixture<EventDataDialogComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [EventDataDialogComponent]
        })
            .compileComponents();

        fixture = TestBed.createComponent(EventDataDialogComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
