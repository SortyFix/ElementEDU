import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetupSMSComponent } from './setup-sms.component';

describe('SetupSMSComponent', () => {
  let component: SetupSMSComponent;
  let fixture: ComponentFixture<SetupSMSComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SetupSMSComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SetupSMSComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
