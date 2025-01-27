import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IllnessNotificationComponent } from './illness-notification.component';

describe('IllnessNotificationComponent', () => {
  let component: IllnessNotificationComponent;
  let fixture: ComponentFixture<IllnessNotificationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IllnessNotificationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IllnessNotificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
