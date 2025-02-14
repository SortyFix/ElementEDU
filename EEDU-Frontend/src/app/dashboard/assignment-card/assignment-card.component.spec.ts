import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignmentCardComponent } from './assignment-card.component';

describe('HomeworkCardComponent', () => {
  let component: AssignmentCardComponent;
  let fixture: ComponentFixture<AssignmentCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AssignmentCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AssignmentCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
