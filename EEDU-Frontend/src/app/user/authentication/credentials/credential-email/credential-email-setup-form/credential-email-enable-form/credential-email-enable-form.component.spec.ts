import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CredentialEmailEnableFormComponent } from './credential-email-enable-form.component';

describe('CredentialEnableEmailFormComponent', () => {
  let component: CredentialEmailEnableFormComponent;
  let fixture: ComponentFixture<CredentialEmailEnableFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CredentialEmailEnableFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CredentialEmailEnableFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
