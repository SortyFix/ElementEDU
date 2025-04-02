import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InsightListComponent } from './insight-list.component';

describe('InsightListComponent', () => {
  let component: InsightListComponent;
  let fixture: ComponentFixture<InsightListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InsightListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InsightListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
