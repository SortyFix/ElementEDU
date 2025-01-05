import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateStandaloneAppointmentComponent } from './create-standalone-appointment.component';

describe('CreateStandaloneAppointmentComponent', () => {
  let component: CreateStandaloneAppointmentComponent;
  let fixture: ComponentFixture<CreateStandaloneAppointmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateStandaloneAppointmentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateStandaloneAppointmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
