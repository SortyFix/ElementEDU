import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CredentialTotpVerifyFormComponent } from './credential-totp-verify-form.component';

describe('CredentialTotpFormComponent', () => {
  let component: CredentialTotpVerifyFormComponent;
  let fixture: ComponentFixture<CredentialTotpVerifyFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CredentialTotpVerifyFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CredentialTotpVerifyFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
