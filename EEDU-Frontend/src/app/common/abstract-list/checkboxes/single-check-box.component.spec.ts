import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SingleCheckBoxComponent } from './single-check-box.component';

describe('SingleCheckBoxComponent', () => {
  let component: SingleCheckBoxComponent;
  let fixture: ComponentFixture<SingleCheckBoxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SingleCheckBoxComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SingleCheckBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
