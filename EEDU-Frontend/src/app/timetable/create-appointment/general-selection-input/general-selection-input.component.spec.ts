import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralSelectionInput } from './general-selection-input.component';

describe('CourseSelectorComponent', () => {
  let component: GeneralSelectionInput;
  let fixture: ComponentFixture<GeneralSelectionInput>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GeneralSelectionInput]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GeneralSelectionInput);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
