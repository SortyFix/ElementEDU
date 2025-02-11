import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AbstractItemListComponent } from './abstract-item-list.component';

describe('AbstractItemListComponent', () => {
  let component: AbstractItemListComponent<any>;
  let fixture: ComponentFixture<AbstractItemListComponent<any>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AbstractItemListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AbstractItemListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
