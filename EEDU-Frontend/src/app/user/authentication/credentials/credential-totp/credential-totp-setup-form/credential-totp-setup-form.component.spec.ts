import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CredentialTotpSetupFormComponent } from './credential-totp-setup-form.component';

describe('CredentialTotpSetupFormComponent', () => {
  let component: CredentialTotpSetupFormComponent;
  let fixture: ComponentFixture<CredentialTotpSetupFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CredentialTotpSetupFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CredentialTotpSetupFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
