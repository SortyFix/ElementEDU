import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginNameFormComponent } from './login-name-form.component';

describe('LoginNameFormComponent', () => {
  let component: LoginNameFormComponent;
  let fixture: ComponentFixture<LoginNameFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginNameFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(LoginNameFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
