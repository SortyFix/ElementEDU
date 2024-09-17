import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetupEmailCredentialComponent } from './setup-email-credential.component';

describe('SetupEmailCredentialComponent', () => {
  let component: SetupEmailCredentialComponent;
  let fixture: ComponentFixture<SetupEmailCredentialComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SetupEmailCredentialComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SetupEmailCredentialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
