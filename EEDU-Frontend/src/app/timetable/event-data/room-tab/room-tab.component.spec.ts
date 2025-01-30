import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomTabComponent } from './room-tab.component';

describe('RoomTabComponent', () => {
  let component: RoomTabComponent;
  let fixture: ComponentFixture<RoomTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoomTabComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoomTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
