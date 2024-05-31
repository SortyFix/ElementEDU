import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestLoginComponent } from './request-login.component';

describe('LoginComponent', () => {
  let component: RequestLoginComponent;
  let fixture: ComponentFixture<RequestLoginComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RequestLoginComponent]
    });
    fixture = TestBed.createComponent(RequestLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
