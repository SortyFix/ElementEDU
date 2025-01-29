import { TestBed } from '@angular/core/testing';

import { ClassRoomService } from './class-room.service';

describe('ClassRoomService', () => {
  let service: ClassRoomService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClassRoomService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
