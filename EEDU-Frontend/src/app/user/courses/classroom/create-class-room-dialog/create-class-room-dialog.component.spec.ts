import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateClassRoomDialogComponent } from './create-class-room-dialog.component';

describe('CreateClassRoomComponent', () => {
  let component: CreateClassRoomDialogComponent;
  let fixture: ComponentFixture<CreateClassRoomDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateClassRoomDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateClassRoomDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
