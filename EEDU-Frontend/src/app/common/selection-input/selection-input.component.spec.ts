import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectionInput } from './selection-input.component';

describe('CourseSelectorComponent', () => {
  let component: SelectionInput<any>;
  let fixture: ComponentFixture<SelectionInput<any>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectionInput]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectionInput);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
