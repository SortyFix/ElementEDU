import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SelectCredentialComponent} from './select-credential.component';

describe('SelectCredentialComponent', () => {
    let component: SelectCredentialComponent;
    let fixture: ComponentFixture<SelectCredentialComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [SelectCredentialComponent]
        })
            .compileComponents();

        fixture = TestBed.createComponent(SelectCredentialComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
