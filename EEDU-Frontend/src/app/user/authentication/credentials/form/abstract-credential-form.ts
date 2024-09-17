import {Component, EventEmitter, Input, Output, signal, WritableSignal} from "@angular/core";
import {LoginData} from "../../login-data/login-data";
import {AbstractLoginForm} from "../../abstract-login-form";

@Component({standalone: true, template: ''})
export abstract class AbstractCredentialForm<T> extends AbstractLoginForm<T>
{

    @Input() public _loginData?: LoginData;
    @Input() private _errorSignal: WritableSignal<any> = signal('');

    protected get errorSignal(): WritableSignal<any>
    {
        return this._errorSignal;
    }

    protected get loginData(): LoginData | undefined
    {
        return this._loginData;
    }

    protected onCancel(): void
    {
        this._loginData = undefined;
        this.submit.emit(false);
    };
}
