import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CredentialTotpFormComponent } from './credential-totp-form.component';

describe('CredentialTotpFormComponent', () => {
  let component: CredentialTotpFormComponent;
  let fixture: ComponentFixture<CredentialTotpFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CredentialTotpFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CredentialTotpFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
