import {Component, forwardRef, input, Input, InputSignal} from '@angular/core';
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from "@angular/material/datepicker";
import {MatInput} from "@angular/material/input";
import {MatTimepicker, MatTimepickerInput, MatTimepickerToggle} from "@angular/material/timepicker";
import {ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR} from "@angular/forms";

@Component({
    selector: 'app-date-time-picker',
    standalone: true,
    imports: [
        MatFormField,
        MatDatepicker,
        MatDatepickerInput,
        MatDatepickerToggle,
        MatInput,
        MatTimepickerInput,
        MatTimepicker,
        MatTimepickerToggle,
        FormsModule,
        MatLabel,
        MatSuffix
    ],
    providers:  [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef((): typeof DateTimePickerComponent => DateTimePickerComponent),
            multi: true
        }
    ],
    templateUrl: './date-time-picker.component.html',
    styleUrl: './date-time-picker.component.scss'
})
export class DateTimePickerComponent implements ControlValueAccessor {
    public readonly label: InputSignal<string> = input<string>('');
    public readonly minDate: InputSignal<Date | null> = input<Date | null>(null);

    private _date: Date | null = null;
    private _time: Date | null = null;

    private onChange: (value: Date | null) => void = () => {};
    private onTouched: () => void = () => {};

    public updateValue() {
        if (this.date && this.time) {
            const resultDate = new Date(this.date); // Copy date
            resultDate.setHours(this.time.getHours(), this.time.getMinutes()); // Set time
            this.onChange(resultDate);
            return;
        }

        this.onChange(null);
    }

    public writeValue(value: Date | null): void {
        if (!value) {
            this.reset();
            return;
        }

        const date: Date = new Date(value);

        this.date = date;
        this.time = date;
    }

    public reset(): void
    {
        this.date = null;
        this.time = null;
    }

    public registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    public registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    public setDisabledState?(isDisabled: boolean): void {
        // Handle disabled state if needed
    }

    protected get date(): Date | null {
        return this._date;
    }

    protected set date(value: Date | null) {
        this._date = value;
        this.updateValue();
    }

    protected get time(): Date | null {
        return this._time;
    }

    protected set time(value: Date | null) {
        this._time = value;
        this.updateValue();
    }
}
