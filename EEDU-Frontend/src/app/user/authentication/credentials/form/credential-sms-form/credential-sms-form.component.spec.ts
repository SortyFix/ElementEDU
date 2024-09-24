import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CredentialSmsFormComponent } from './credential-sms-form.component';

describe('CredentialSmsFormComponent', () => {
  let component: CredentialSmsFormComponent;
  let fixture: ComponentFixture<CredentialSmsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CredentialSmsFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CredentialSmsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
