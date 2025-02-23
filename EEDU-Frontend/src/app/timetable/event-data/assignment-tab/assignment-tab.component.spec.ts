import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AssignmentTabComponent} from './assignment-tab.component';

describe('AssignmentTabComponent', () => {
    let component: AssignmentTabComponent;
    let fixture: ComponentFixture<AssignmentTabComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [AssignmentTabComponent]
        })
            .compileComponents();

        fixture = TestBed.createComponent(AssignmentTabComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
