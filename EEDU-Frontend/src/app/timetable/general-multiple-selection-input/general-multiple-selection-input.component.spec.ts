import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralMultipleSelectionInput } from './general-multiple-selection-input.component';

describe('CourseSelectorComponent', () => {
  let component: GeneralMultipleSelectionInput<any>;
  let fixture: ComponentFixture<GeneralMultipleSelectionInput<any>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GeneralMultipleSelectionInput]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GeneralMultipleSelectionInput);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
