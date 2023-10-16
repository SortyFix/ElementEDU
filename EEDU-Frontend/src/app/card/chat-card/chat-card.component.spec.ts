import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ChatCardComponent} from './chat-card.component';
import {MatCardModule} from "@angular/material/card";
import {MatMenuModule} from "@angular/material/menu";
import {MatIconModule} from "@angular/material/icon";

describe('ChatCardComponent', () => {
    let component: ChatCardComponent;
    let fixture: ComponentFixture<ChatCardComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [
                ChatCardComponent
            ],
            imports: [
                MatCardModule,
                MatMenuModule,
                MatIconModule,
            ]
        });
        fixture = TestBed.createComponent(ChatCardComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
