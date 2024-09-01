import {Component, EventEmitter, Input, Output} from '@angular/core';
import {LoginData} from "./login-data/login-data";

@Component({standalone: true, template: ''})
export abstract class AbstractCodeCredential
{

    @Output() private readonly _submit = new EventEmitter<any>();
    @Input() public _loginData?: LoginData;
    protected _code: string | undefined = undefined;

    protected abstract onSubmit(): void;

    protected get loginData(): LoginData | undefined
    {
        return this._loginData;
    }

    protected emit(data: any): void
    {
        this._submit.emit(data);
    }

    protected onCancel(): void
    {
        this._loginData = undefined;
        this._submit.emit(false);
    };

}
