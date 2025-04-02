import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LazyLoadedAccordionComponent } from './lazy-loaded-accordion.component';

describe('LazyLoadedAccordionComponent', () => {
  let component: LazyLoadedAccordionComponent;
  let fixture: ComponentFixture<LazyLoadedAccordionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LazyLoadedAccordionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LazyLoadedAccordionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
