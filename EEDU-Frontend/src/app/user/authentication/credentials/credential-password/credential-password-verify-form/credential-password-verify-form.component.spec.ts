import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CredentialPasswordVerifyFormComponent } from './credential-password-verify-form.component';

describe('PasswordFormComponent', () => {
  let component: CredentialPasswordVerifyFormComponent;
  let fixture: ComponentFixture<CredentialPasswordVerifyFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CredentialPasswordVerifyFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CredentialPasswordVerifyFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
