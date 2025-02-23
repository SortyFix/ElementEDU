import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {DialogRef} from "@angular/cdk/dialog";
import {AbstractCourseComponentsService} from "../abstract-course-components-service";

export abstract class AbstractCourseComponentsCreate<T> {

    private readonly _title: string;

    private readonly _values: T[] = [];
    private readonly _form: FormGroup;

    protected constructor
    (
        private _service: AbstractCourseComponentsService<any, any, any>,
        private _dialogRef: DialogRef, formBuilder: FormBuilder,
        title: string
    ) {
        this._form = this.getForm(formBuilder);
        this._service.value$.subscribe((values: T[]): void => { this.values = values; });
        this._title = title;
    }

    protected get title(): string {
        return this._title;
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
        return formBuilder.group({ id: [null, Validators.required]});
    }

    protected onSubmit(): void {
        if (!this.canSubmit) {
            return;
        }

        this._service.create(this.createModel).subscribe((): void => { this._dialogRef.close(); })
    }

    protected get createModel(): any[] { return [this.form.value]; }

    protected get canSubmit(): boolean { return this.form.valid; }
}
