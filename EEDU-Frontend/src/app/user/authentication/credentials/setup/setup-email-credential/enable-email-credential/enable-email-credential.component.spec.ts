import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnableEmailCredentialComponent } from './enable-email-credential.component';

describe('EnableEmailCredentialComponent', () => {
  let component: EnableEmailCredentialComponent;
  let fixture: ComponentFixture<EnableEmailCredentialComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnableEmailCredentialComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EnableEmailCredentialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
