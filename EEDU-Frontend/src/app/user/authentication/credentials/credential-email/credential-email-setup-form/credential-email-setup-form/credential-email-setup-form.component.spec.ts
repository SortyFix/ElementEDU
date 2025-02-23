import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CredentialEmailSetupFormComponent} from './credential-email-setup-form.component';

describe('CredentialSetupEmailFormComponent', () => {
    let component: CredentialEmailSetupFormComponent;
    let fixture: ComponentFixture<CredentialEmailSetupFormComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [CredentialEmailSetupFormComponent]
        })
            .compileComponents();

        fixture = TestBed.createComponent(CredentialEmailSetupFormComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
