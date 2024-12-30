import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateScheduledAppointmentComponent } from './create-scheduled-appointment.component';

describe('CreateScheduledAppointmentComponent', () => {
  let component: CreateScheduledAppointmentComponent;
  let fixture: ComponentFixture<CreateScheduledAppointmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateScheduledAppointmentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateScheduledAppointmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
