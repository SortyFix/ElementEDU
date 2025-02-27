import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GeneralCardComponent} from './general-card.component';

describe('GeneralCreateComponentComponent', () => {
    let component: GeneralCardComponent;
    let fixture: ComponentFixture<GeneralCardComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [GeneralCardComponent]
        })
            .compileComponents();

        fixture = TestBed.createComponent(GeneralCardComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
