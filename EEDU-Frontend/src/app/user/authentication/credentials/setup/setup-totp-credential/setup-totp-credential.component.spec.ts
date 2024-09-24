import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetupTotpCredentialComponent } from './setup-totp-credential.component';

describe('SetupTotpCredentialComponent', () => {
  let component: SetupTotpCredentialComponent;
  let fixture: ComponentFixture<SetupTotpCredentialComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SetupTotpCredentialComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SetupTotpCredentialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
