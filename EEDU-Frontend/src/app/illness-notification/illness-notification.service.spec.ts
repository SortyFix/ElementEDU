import { TestBed } from '@angular/core/testing';

import { IllnessNotificationService } from './illness-notification.service';

describe('IllnessNotificationService', () => {
  let service: IllnessNotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IllnessNotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
