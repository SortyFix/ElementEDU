import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CredentialSmsEnableFormComponent } from './credential-sms-enable-form.component';

describe('CredentialEnableSmsFormComponent', () => {
  let component: CredentialSmsEnableFormComponent;
  let fixture: ComponentFixture<CredentialSmsEnableFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CredentialSmsEnableFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CredentialSmsEnableFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
