import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChatCreationComponent } from './chat-creation.component';

describe('ChatCreationComponent', () => {
  let component: ChatCreationComponent;
  let fixture: ComponentFixture<ChatCreationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChatCreationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChatCreationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
