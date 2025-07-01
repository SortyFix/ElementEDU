import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralErrorBoxComponent } from './general-error-box.component';

describe('GeneralErrorBoxComponent', () => {
  let component: GeneralErrorBoxComponent;
  let fixture: ComponentFixture<GeneralErrorBoxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GeneralErrorBoxComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GeneralErrorBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
