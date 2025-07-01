import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagementCourseSectionComponent } from './management-course-section.component';

describe('ManagementCourseSectionComponent', () => {
  let component: ManagementCourseSectionComponent;
  let fixture: ComponentFixture<ManagementCourseSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagementCourseSectionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagementCourseSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
