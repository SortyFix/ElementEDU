import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetupEmailComponent } from './setup-email.component';

describe('SetupEmailComponent', () => {
  let component: SetupEmailComponent;
  let fixture: ComponentFixture<SetupEmailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SetupEmailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SetupEmailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
