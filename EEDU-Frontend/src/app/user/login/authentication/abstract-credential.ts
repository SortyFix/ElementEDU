import {Component, EventEmitter, Input, Output, signal, WritableSignal} from '@angular/core';
import {LoginData} from "./login-data/login-data";
import {FormGroup} from "@angular/forms";

@Component({standalone: true, template: ''})
export abstract class AbstractCredential<T>
{

    @Output() private readonly submit = new EventEmitter<T | boolean>();
    @Input() public _loginData?: LoginData;
    @Input() private readonly _form: FormGroup;
    @Input() private _errorSignal: WritableSignal<any> = signal('');

    protected get errorSignal(): WritableSignal<any>
    {
        return this._errorSignal;
    }

    protected constructor(form: FormGroup)
    {
        this._form = form;
    }

    protected get form(): FormGroup
    {
        return this._form;
    }

    protected abstract onSubmit(): void;

    protected get loginData(): LoginData | undefined
    {
        return this._loginData;
    }

    protected emit(data: T): void
    {
        this.submit.emit(data);
    }

    protected onCancel(): void
    {
        this._loginData = undefined;
        this.submit.emit(false);
    };
}
