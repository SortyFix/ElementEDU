import {Component, Inject, signal, WritableSignal} from "@angular/core";
import {FormGroup} from "@angular/forms";
import {merge} from "rxjs";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {AuthenticationService} from "./authentication.service";

@Component({standalone: true, template: ''})
export abstract class AbstractLoginForm {

    private readonly _errorMessageSignal: WritableSignal<string | undefined>;
    private readonly _form: FormGroup;

    protected constructor(form: FormGroup, protected authenticationService: AuthenticationService) {
        this._errorMessageSignal = signal(undefined);
        this._form = form;
    }

    protected registerField(@Inject(String) fieldName: string): void {
        merge(this.form.get(fieldName)!.statusChanges, this.form.get(fieldName)!.valueChanges).pipe(takeUntilDestroyed()).subscribe(() => this.updateErrorMessage(fieldName));
    }

    protected get errorMessageSignal(): WritableSignal<string | undefined> {
        return this._errorMessageSignal;
    }

    protected get form(): FormGroup {
        return this._form;
    }

    protected exceptionHandler(fieldName: string) {
        return {error: (error: any): void => {
            let status: number = 0;
            if(typeof error === 'object' && 'status' in error && typeof error.status === 'number')
            {
                status = error.status;
            }
            this.error = {field: fieldName, serverError: this.errorMessage(status)}
        }}
    }

    protected errorMessage(status: number): string
    {
        return "An unknown error occurred."
    }

    protected updateErrorMessage(fieldName: string): void {
        const field = this.form.get(fieldName)!;

        if (field.hasError('required')) {
            this.errorMessageSignal.set("This field is required");
            return;
        }

        if (field.hasError('serverError')) {
            this.errorMessageSignal.set(field.getError('serverError'));
            return;
        }

        this.errorMessageSignal.set(undefined);
    }

    private set error(data: { field: string, serverError: string }) {
        const field: string = data.field;
        const serverError: string = data.serverError;
        this.form.get(field)!.setErrors({serverError});
    }

    protected abstract onSubmit(): void;
}
