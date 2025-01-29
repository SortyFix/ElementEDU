import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AbstractSimpleCourseService} from "./abstract-simple-course-service";
import {DialogRef} from "@angular/cdk/dialog";

export abstract class AbstractCreateComponent<T> {
    private readonly _values: T[] = [];
    private readonly _form: FormGroup;

    protected constructor(private _service: AbstractSimpleCourseService<any, any>, private _dialogRef: DialogRef, formBuilder: FormBuilder) {
        this._form = this.getForm(formBuilder);
        this._service.value$.subscribe((values: T[]) => { this.values = values; });
    }

    protected get form(): FormGroup {
        return this._form;
    }

    private set values(value: T[]) {
        this._values.length = 0;
        this._values.push(...value);
    }

    protected get values(): T[] {
        return this._values;
    }

    protected get loading(): boolean {
        return !this._service.fetched;
    }

    protected getForm(formBuilder: FormBuilder): FormGroup {
        return formBuilder.group({name: [null, Validators.required]});
    }

    protected onSubmit(): void {
        if (!this.canSubmit) {
            return;
        }

        this._service.create([this.form.value]).subscribe((): void => { this._dialogRef.close(); })
    }

    protected get canSubmit(): boolean {
        return this.form.valid;
    }
}
