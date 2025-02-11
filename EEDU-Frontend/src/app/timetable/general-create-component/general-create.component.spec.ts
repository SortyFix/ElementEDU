import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralCreateComponent } from './general-create.component';

describe('GeneralCreateComponentComponent', () => {
  let component: GeneralCreateComponent;
  let fixture: ComponentFixture<GeneralCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GeneralCreateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GeneralCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
