import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagementUserSectionComponent } from './management-user-section.component';

describe('ManagementUserSectionComponent', () => {
  let component: ManagementUserSectionComponent;
  let fixture: ComponentFixture<ManagementUserSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagementUserSectionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagementUserSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
