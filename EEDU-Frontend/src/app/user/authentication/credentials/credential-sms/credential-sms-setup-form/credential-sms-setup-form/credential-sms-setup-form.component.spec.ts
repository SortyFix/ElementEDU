import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CredentialSmsSetupFormComponent} from './credential-sms-setup-form.component';

describe('CredentialSetupSmsFormComponent', () => {
    let component: CredentialSmsSetupFormComponent;
    let fixture: ComponentFixture<CredentialSmsSetupFormComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [CredentialSmsSetupFormComponent]
        })
            .compileComponents();

        fixture = TestBed.createComponent(CredentialSmsSetupFormComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
