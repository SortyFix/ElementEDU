import {Component, EventEmitter, input, Input, InputSignal, Output, signal, WritableSignal} from "@angular/core";
import {LoginData} from "../../login-data/login-data";
import {AbstractLoginForm} from "../../abstract-login-form";

@Component({standalone: true, template: ''})
export abstract class AbstractCredentialForm<T> extends AbstractLoginForm<T>
{
    public readonly loginData: InputSignal<LoginData | undefined> = input<LoginData>();

    protected onCancel(): void
    {
        this.submit.emit(false);
    };
}
