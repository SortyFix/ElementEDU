import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AbstractList } from './abstract-list.component';

describe('AbstractListImplementationComponent', () => {
  let component: AbstractList<any>;
  let fixture: ComponentFixture<AbstractList<any>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AbstractList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AbstractList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
