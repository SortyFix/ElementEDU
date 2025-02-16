import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FullUserListComponent } from './full-user-list.component';

describe('FullUserListComponent', () => {
  let component: FullUserListComponent;
  let fixture: ComponentFixture<FullUserListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FullUserListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FullUserListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
