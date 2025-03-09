import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SimpleCreateDialogComponent } from './simple-create-dialog.component';

describe('SCreateDialogComponent', () => {
  let component: SimpleCreateDialogComponent;
  let fixture: ComponentFixture<SimpleCreateDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SimpleCreateDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SimpleCreateDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
