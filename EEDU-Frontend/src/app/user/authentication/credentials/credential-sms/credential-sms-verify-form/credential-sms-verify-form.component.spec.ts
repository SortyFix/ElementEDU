import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CredentialSmsVerifyFormComponent} from './credential-sms-verify-form.component';

describe('CredentialSmsFormComponent', () => {
    let component: CredentialSmsVerifyFormComponent;
    let fixture: ComponentFixture<CredentialSmsVerifyFormComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [CredentialSmsVerifyFormComponent]
        })
            .compileComponents();

        fixture = TestBed.createComponent(CredentialSmsVerifyFormComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
