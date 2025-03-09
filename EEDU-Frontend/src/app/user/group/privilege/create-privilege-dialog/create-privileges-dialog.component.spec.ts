import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreatePrivilegesDialogComponent } from './create-privileges-dialog.component';

describe('CreatePrivilegesComponent', () => {
  let component: CreatePrivilegesDialogComponent;
  let fixture: ComponentFixture<CreatePrivilegesDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreatePrivilegesDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreatePrivilegesDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
