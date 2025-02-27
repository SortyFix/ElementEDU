import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteClassRoomComponent } from './delete-class-room.component';

describe('DeleteClassRoomComponent', () => {
  let component: DeleteClassRoomComponent;
  let fixture: ComponentFixture<DeleteClassRoomComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteClassRoomComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeleteClassRoomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
