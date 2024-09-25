import {
    Component,
    EventEmitter,
    Inject,
    input,
    Input,
    InputSignal,
    Output,
    signal,
    WritableSignal
} from "@angular/core";
import {LoginData} from "../../login-data/login-data";
import {AbstractLoginForm} from "../../abstract-login-form";
import {AuthenticationService} from "../../authentication.service";
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({standalone: true, template: ''})
export abstract class AbstractCredentialForm<T> extends AbstractLoginForm<T> {
    protected constructor(formGroup: FormGroup, @Inject(String) fieldName: string, authenticationService: AuthenticationService) {
        super(formGroup, fieldName, authenticationService);
    }

    protected get loginData(): LoginData | undefined {
        return this.authenticationService.loginData;
    }

    protected onCancel(): void {
        this.authenticationService.reset();
    };
}
