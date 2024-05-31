import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectCredentialsComponent } from './select-credentials.component';

describe('SelectCredentialsComponent', () => {
  let component: SelectCredentialsComponent;
  let fixture: ComponentFixture<SelectCredentialsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectCredentialsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SelectCredentialsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
