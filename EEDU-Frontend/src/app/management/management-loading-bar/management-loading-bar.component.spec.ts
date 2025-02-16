import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagementLoadingBar } from './management-loading-bar.component';

describe('TimetableCommonsComponent', () => {
  let component: ManagementLoadingBar;
  let fixture: ComponentFixture<ManagementLoadingBar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagementLoadingBar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagementLoadingBar);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
