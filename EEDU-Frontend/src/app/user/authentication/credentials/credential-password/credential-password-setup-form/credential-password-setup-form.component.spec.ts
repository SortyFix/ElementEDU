import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CredentialPasswordSetupFormComponent } from './credential-password-setup-form.component';

describe('CredentialPasswordSetupFormComponent', () => {
  let component: CredentialPasswordSetupFormComponent;
  let fixture: ComponentFixture<CredentialPasswordSetupFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CredentialPasswordSetupFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CredentialPasswordSetupFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
