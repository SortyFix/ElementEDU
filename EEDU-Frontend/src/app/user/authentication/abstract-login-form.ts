import {Component, effect, Inject, input, output, signal, WritableSignal} from "@angular/core";
import {FormGroup} from "@angular/forms";
import {merge} from "rxjs";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {AuthenticationService} from "./authentication.service";

@Component({standalone: true, template: ''})
export abstract class AbstractLoginForm<T>  {

    private readonly _fieldName: string;
    private readonly _errorMessageSignal: WritableSignal<string | undefined>;
    private readonly _form: FormGroup;

    protected constructor(form: FormGroup, @Inject(String) fieldName: string, protected authenticationService: AuthenticationService)
    {
        this._errorMessageSignal = signal(undefined);
        this._form = form;
        this._fieldName = fieldName;

        merge(this.form.get(fieldName)!.statusChanges, this.form.get(fieldName)!.valueChanges).pipe(takeUntilDestroyed()).subscribe(() => this.updateErrorMessage());

        effect(() =>
        {
            const serverError: string | undefined = this.errorMessage;
            if (!!serverError)
            {
                this.error = serverError;
            }
        });
    }

    protected get errorMessageSignal(): WritableSignal<string | undefined>
    {
        return this._errorMessageSignal;
    }

    protected get form(): FormGroup
    {
        return this._form;
    }

    protected updateErrorMessage(): void
    {
        const field = this.form.get(this._fieldName)!;

        if(field.hasError('required'))
        {
            this.errorMessageSignal.set("This field is required");
            return;
        }

        if(field.hasError('serverError'))
        {
            this.errorMessageSignal.set(field.getError('serverError'));
            return;
        }

        this.errorMessageSignal.set(undefined);
    }

    protected get fieldName(): string
    {
        return this._fieldName;
    }

    protected get errorMessage(): string | undefined
    {
        return undefined;
    }


    protected set error(serverError: string)
    {
        this.form.get(this._fieldName)!.setErrors({serverError});
    }

    protected abstract onSubmit(): void;
}
