import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HausaufgabenCardComponent } from './hausaufgaben-card.component';

describe('HausaufgabenCardComponent', () => {
  let component: HausaufgabenCardComponent;
  let fixture: ComponentFixture<HausaufgabenCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HausaufgabenCardComponent]
    });
    fixture = TestBed.createComponent(HausaufgabenCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
