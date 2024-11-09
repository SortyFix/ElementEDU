import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupSelectionList } from './group-selection-list.component';

describe('GroupListComponent', () => {
  let component: GroupSelectionList;
  let fixture: ComponentFixture<GroupSelectionList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GroupSelectionList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GroupSelectionList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
