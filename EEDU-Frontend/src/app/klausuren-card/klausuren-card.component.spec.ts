import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KlausurenCardComponent } from './klausuren-card.component';

describe('KlausurenCardComponent', () => {
  let component: KlausurenCardComponent;
  let fixture: ComponentFixture<KlausurenCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [KlausurenCardComponent]
    });
    fixture = TestBed.createComponent(KlausurenCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
