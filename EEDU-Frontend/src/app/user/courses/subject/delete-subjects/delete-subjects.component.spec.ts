import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteSubjectsComponent } from './delete-subjects.component';

describe('DeleteSubjectsComponent', () => {
  let component: DeleteSubjectsComponent;
  let fixture: ComponentFixture<DeleteSubjectsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteSubjectsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeleteSubjectsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
