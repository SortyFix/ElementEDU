import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CredentialEmailVerifyFormComponent } from './credential-email-verify-form.component';

describe('CredentialEmailFormComponent', () => {
  let component: CredentialEmailVerifyFormComponent;
  let fixture: ComponentFixture<CredentialEmailVerifyFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CredentialEmailVerifyFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CredentialEmailVerifyFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
