import {Component, forwardRef, input, InputSignal} from '@angular/core';
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatAutocomplete, MatAutocompleteTrigger, MatOption} from "@angular/material/autocomplete";
import {MatInput} from "@angular/material/input";
import {AsyncPipe, NgForOf} from "@angular/common";
import {map, Observable, startWith, Subscriber} from "rxjs";
import {ControlValueAccessor, FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR, Validator} from "@angular/forms";

@Component({
    selector: 'app-course-selector',
    standalone: true,
    imports: [MatFormField, MatAutocompleteTrigger, MatInput, MatAutocomplete, MatOption, NgForOf, FormsModule, AsyncPipe, MatLabel],
    providers: [{
        provide: NG_VALUE_ACCESSOR, useExisting: forwardRef((): typeof CourseSelectorComponent => CourseSelectorComponent), multi: true,
    }, {
        provide: NG_VALIDATORS, useExisting: forwardRef((): typeof CourseSelectorComponent=> CourseSelectorComponent), multi: true,
    },],
    templateUrl: './course-selector.component.html',
    styleUrl: './course-selector.component.scss'
})
export class CourseSelectorComponent implements ControlValueAccessor, Validator {

    public label: InputSignal<string> = input<string>('');
    public placeholder: InputSignal<string> = input<string>('');
    public values: InputSignal<{ name: string }[]> = input<{ name: string }[]>([]);

    private readonly _filteredValues!: Observable<{ name: string }[]>;

    private _value: string = '';
    private onChange: (value: string) => void = (): void => {};
    protected onTouched: () => void = (): void => {};

    constructor() {
        this._filteredValues = new Observable<string>((observer: Subscriber<string>): void =>
        {
            observer.next(this.value)
        }).pipe(startWith(''), map((value: string): { name: string }[] => this.filterCourses(value)));
    }

    private filterCourses(value: string): { name: string }[] {
        const filterValue: string = value.toLowerCase();
        return this.values().filter((value: { name: string }): boolean => value.name.toLowerCase().includes(filterValue));
    }

    public onInputChange(value: string): void {
        this._value = value;
        this.onChange(value);
    }

    public writeValue(value: string): void {
        this._value = value || '';
    }

    public registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    public registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    public validate(): { invalidCourse: boolean } | null {
        const isValid: boolean = this.values().some((course: { name: string }): boolean => course.name === this.value);
        return isValid ? null : {invalidCourse: true};
    }

    protected get value(): string {
        return this._value;
    }


    protected get filteredValues(): Observable<{ name: string }[]> {
        return this._filteredValues;
    }

    protected set value(value: string) {
        this._value = value;
    }
}
