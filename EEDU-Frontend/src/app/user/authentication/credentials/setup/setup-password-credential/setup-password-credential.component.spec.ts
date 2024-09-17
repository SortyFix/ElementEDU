import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetupPasswordCredentialComponent } from './setup-password-credential.component';

describe('SetupPasswordCredentialComponent', () => {
  let component: SetupPasswordCredentialComponent;
  let fixture: ComponentFixture<SetupPasswordCredentialComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SetupPasswordCredentialComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SetupPasswordCredentialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
