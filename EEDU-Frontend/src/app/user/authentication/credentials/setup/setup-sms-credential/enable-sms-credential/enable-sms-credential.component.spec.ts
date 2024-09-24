import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnableSmsCredentialComponent } from './enable-sms-credential.component';

describe('EnableSmsCredentialComponent', () => {
  let component: EnableSmsCredentialComponent;
  let fixture: ComponentFixture<EnableSmsCredentialComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnableSmsCredentialComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EnableSmsCredentialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
