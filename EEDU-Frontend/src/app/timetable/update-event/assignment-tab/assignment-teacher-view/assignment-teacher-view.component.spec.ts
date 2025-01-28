import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignmentTeacherViewComponent } from './assignment-teacher-view.component';

describe('AssignmentTeacherViewComponent', () => {
  let component: AssignmentTeacherViewComponent;
  let fixture: ComponentFixture<AssignmentTeacherViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AssignmentTeacherViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AssignmentTeacherViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
