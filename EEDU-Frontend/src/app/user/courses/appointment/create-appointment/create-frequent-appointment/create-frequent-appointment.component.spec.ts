import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CreateFrequentAppointmentComponent} from './create-frequent-appointment.component';

describe('CreateScheduledAppointmentComponent', () => {
    let component: CreateFrequentAppointmentComponent;
    let fixture: ComponentFixture<CreateFrequentAppointmentComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [CreateFrequentAppointmentComponent]
        })
            .compileComponents();

        fixture = TestBed.createComponent(CreateFrequentAppointmentComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
