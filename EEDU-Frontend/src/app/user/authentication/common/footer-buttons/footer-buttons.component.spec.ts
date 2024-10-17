import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FooterButtonsComponent } from './footer-buttons.component';

describe('FooterButtonsComponent', () => {
  let component: FooterButtonsComponent;
  let fixture: ComponentFixture<FooterButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FooterButtonsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FooterButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
