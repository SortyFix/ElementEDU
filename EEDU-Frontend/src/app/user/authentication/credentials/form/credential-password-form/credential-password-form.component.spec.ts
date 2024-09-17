import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CredentialPasswordFormComponent } from './credential-password-form.component';

describe('PasswordFormComponent', () => {
  let component: CredentialPasswordFormComponent;
  let fixture: ComponentFixture<CredentialPasswordFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CredentialPasswordFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CredentialPasswordFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
