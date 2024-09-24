import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CredentialEmailFormComponent } from './credential-email-form.component';

describe('CredentialEmailFormComponent', () => {
  let component: CredentialEmailFormComponent;
  let fixture: ComponentFixture<CredentialEmailFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CredentialEmailFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CredentialEmailFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
