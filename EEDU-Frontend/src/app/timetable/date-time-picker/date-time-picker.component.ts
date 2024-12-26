import {Component, forwardRef, Input} from '@angular/core';
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
    @Input() label: string = '';

    private _date: Date | null = null;
    private _time: Date | null = null;

    private onChange: (value: Date | null) => void = () => {};
    private onTouched: () => void = () => {};

    public updateValue() {
        if (this.date && this.time) {
            const resultDate = new Date(this.date); // Copy date
            resultDate.setHours(this.time.getHours(), this.time.getMinutes()); // Set time
            this.onChange(resultDate);
        } else {
            this.onChange(null);
        }
    }

    public writeValue(value: Date | null): void {
        if (value) {
            this.date = new Date(value);
            this.time = new Date(value);
        } else {
            this.date = null;
            this.time = null;
        }
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

    get date(): Date | null {
        return this._date;
    }

    set date(value: Date | null) {
        this._date = value;
        this.updateValue();
    }

    get time(): Date | null {
        return this._time;
    }

    set time(value: Date | null) {
        this._time = value;
        this.updateValue();
    }
}
