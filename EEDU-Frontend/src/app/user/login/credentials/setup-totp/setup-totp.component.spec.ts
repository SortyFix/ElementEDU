import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetupTOTPComponent } from './setup-totp.component';

describe('SetupTOTPComponent', () => {
  let component: SetupTOTPComponent;
  let fixture: ComponentFixture<SetupTOTPComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SetupTOTPComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SetupTOTPComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
