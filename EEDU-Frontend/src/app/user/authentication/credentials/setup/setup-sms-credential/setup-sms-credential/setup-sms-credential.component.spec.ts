import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetupSmsCredentialComponent } from './setup-sms-credential.component';

describe('SetupSmsCredentialComponent', () => {
  let component: SetupSmsCredentialComponent;
  let fixture: ComponentFixture<SetupSmsCredentialComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SetupSmsCredentialComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SetupSmsCredentialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
