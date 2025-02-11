import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventTileContentComponent } from './event-tile-content.component';

describe('EventTileContentComponent', () => {
  let component: EventTileContentComponent;
  let fixture: ComponentFixture<EventTileContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventTileContentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventTileContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
