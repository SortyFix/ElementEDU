import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllCheckBoxComponent } from './all-check-box.component';

describe('AllCheckBoxComponent', () => {
  let component: AllCheckBoxComponent;
  let fixture: ComponentFixture<AllCheckBoxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllCheckBoxComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllCheckBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
