import {Component, forwardRef, input, InputSignal, Type} from '@angular/core';
import {MatInput} from "@angular/material/input";
import {
    ControlValueAccessor,
    FormsModule,
    NG_VALIDATORS,
    NG_VALUE_ACCESSOR,
    Validator
} from "@angular/forms";
import {NgIf} from "@angular/common";
import {MatFormField, MatLabel} from "@angular/material/form-field";

/**
 * Enum for different types of duration units
 *
 * This enum represents various units of time, which can be used to specify a duration.
 *
 * @enum
 * @readonly
 */
export enum DurationType {
    YEARS,
    MONTHS,
    WEEKS,
    DAYS,
    HOURS,
    MINUTES,
    SECONDS
}

const type: Type<DurationPickerComponent> = forwardRef((): typeof DurationPickerComponent => DurationPickerComponent);

/**
 * A component for selecting and displaying a duration of time
 *
 * This component allows users to input a time duration in various units.
 * The component implements the {@link ControlValueAccessor} interface, making it to work with Angular forms.
 *
 * @component
 * @example
 * <!-- bind it to a standalone input -->
 * <app-duration-picker [(ngModel)]="selectedDuration"></app-duration-picker>
 *
 * <!-- can also be used within a form -->
 * <form>
 *      <app-duration-picker formControlName="duration"></app-duration-picker>
 * </form>
 * @public
 * @author Ivo Quiring
 */
@Component({
  selector: 'app-duration-picker',
  standalone: true,
    imports: [
        MatInput,
        MatLabel,
        MatFormField,
        FormsModule,
        NgIf
    ],
    providers: [
        { provide: NG_VALUE_ACCESSOR, useExisting: type,  multi: true },
        { provide: NG_VALIDATORS, useExisting: type, multi: true }
    ],
  templateUrl: './duration-picker.component.html',
  styleUrl: './duration-picker.component.scss'
})
export class DurationPickerComponent implements ControlValueAccessor, Validator {

    protected readonly DurationType: typeof DurationType = DurationType;
    public show: InputSignal<DurationType[]> = input<DurationType[]>([DurationType.HOURS, DurationType.MINUTES]);
    public allowNegative: InputSignal<boolean> = input<boolean>(false);

    protected onChange: (value: number) => void = (): void => {};
    protected onTouched: () => void = (): void => {};

    private _years: number = 0;
    private _months: number = 0;
    private _weeks: number = 0;
    private _days: number = 0;
    private _hours: number = 0;
    private _minutes: number = 0;
    private _seconds: number = 0;

    /**
     * Calculates the total time in milliseconds based on the stored time components
     *
     * This getter method converts the individual time components (years, months, weeks, days, hours, minutes, and seconds)
     * into a total value in milliseconds. The calculation is done by multiplying each time unit by the corresponding
     * number of milliseconds, based on standard time conversions (e.g., 1 second = 1000 ms, 1 minute = 60 seconds, etc...).
     *
     * @returns the total time in milliseconds based on the current values of the time components.
     * @private
     */
    private get toMilliseconds(): number {
        const millisecondsInSecond: number = 1000;
        const millisecondsInMinute: number = millisecondsInSecond * 60;
        const millisecondsInHour: number = millisecondsInMinute * 60;
        const millisecondsInDay: number = millisecondsInHour * 24;
        const millisecondsInWeek: number = millisecondsInDay * 7;
        const millisecondsInMonth: number = millisecondsInDay * 30;
        const millisecondsInYear: number = millisecondsInDay * 365;

        return (this._years * millisecondsInYear) +
            (this._months * millisecondsInMonth) +
            (this._weeks * millisecondsInWeek) +
            (this._days * millisecondsInDay) +
            (this._hours * millisecondsInHour) +
            (this._minutes * millisecondsInMinute) +
            (this._seconds * millisecondsInSecond);
    }

    /**
     * Writes a given value (in milliseconds) to update the time components
     *
     * This method accepts a value in milliseconds and converts it into the appropriate time components (years, months, weeks, days, hours, minutes, seconds).
     * It updates the private time properties
     * (
     *      {@link _years},
     *      {@link _months},
     *      {@link _weeks},
     *      {@link _days},
     *      {@link _hours},
     *      {@link _minutes},
     *      and {@link _seconds}
     * ) accordingly.
     * If the provided value is falsy (e.g., null or undefined), the time components are reset to zero.
     *
     * @param value the value in milliseconds to convert and update the time components.
     * @public
     */
    public writeValue(value: number): void {

        if (!value) {
            this.reset();
            return;
        }

        const millisecondsInSecond: number = 1000;
        const millisecondsInMinute: number = millisecondsInSecond * 60;
        const millisecondsInHour: number = millisecondsInMinute * 60;
        const millisecondsInDay: number = millisecondsInHour * 24;
        const millisecondsInWeek: number = millisecondsInDay * 7;
        const millisecondsInMonth: number = millisecondsInDay * 30;
        const millisecondsInYear: number = millisecondsInDay * 365;

        const show: DurationType[] = this.show();

        // reset everything to 0
        this.reset();

        if (show.includes(DurationType.YEARS)) {
            this._years = Math.floor(value / millisecondsInYear);
            value %= millisecondsInYear;
        }

        if (show.includes(DurationType.MONTHS)) {
            this._months = Math.floor(value / millisecondsInMonth);
            value %= millisecondsInMonth;
        }

        if (show.includes(DurationType.WEEKS)) {
            this._weeks = Math.floor(value / millisecondsInWeek);
            value %= millisecondsInWeek;
        }

        if (show.includes(DurationType.DAYS)) {
            this._days = Math.floor(value / millisecondsInDay);
            value %= millisecondsInDay;
        }

        if (show.includes(DurationType.HOURS)) {
            this._hours = Math.floor(value / millisecondsInHour);
            value %= millisecondsInHour;
        }

        if (show.includes(DurationType.MINUTES)) {
            this._minutes = Math.floor(value / millisecondsInMinute);
            value %= millisecondsInMinute;
        }

        if (show.includes(DurationType.SECONDS)) {
            this._seconds = Math.floor(value / millisecondsInSecond);
        }
    }

    /**
     * Registers a callback function to be called when the model value changes
     *
     * This method is used to set the {@link onChange} callback, which will be invoked with the
     * current time value in milliseconds whenever the time properties are updated.
     *
     * @param fn a callback function that accepts the new value as a number (milliseconds).
     * @public
     */
    public registerOnChange(fn: (value: number) => void): void {
        this.onChange = fn;
    }

    /**
     * Registers a callback function to be called when the component is touched
     *
     * This method is used to set the onTouched callback, which will be invoked whenever
     * the user interacts with the component, signaling that it has been touched.
     *
     * @param fn a callback function with no parameters to indicate a touch event.
     * @public
     */
    public registerOnTouched(fn: () => void): void {
        this.onTouched = fn;
    }

    public setDisabledState(isDisabled: boolean): void {
        //TODO
    }

    /**
     * Notifies the change listener of updates to the time values
     *
     * This method checks if the {@link onChange} callback is defined. If it is, the callback is invoked with
     * the current total time value in milliseconds. This ensures that any changes to the time properties
     * are communicated to external listeners.
     *
     * @private
     */
    private notifyChange(): void {
        if (!(this.onChange))
        {
            return;
        }
        this.onChange(this.toMilliseconds);
    }

    /**
     * Resets all time values to their initial state
     *
     * This method sets all private time properties
     * (
     *      {@link _years},
     *      {@link _months},
     *      {@link _weeks},
     *      {@link _days},
     *      {@link _hours},
     *      {@link _minutes},
     *      and {@link _seconds}
     * )
     * to zero. It effectively resets the state of the instance, clearing any previously set time values.
     *
     * @private
     */
    private reset(): void {
        this._years = 0;
        this._months = 0;
        this._weeks = 0;
        this._days = 0;
        this._hours = 0;
        this._minutes = 0;
        this._seconds = 0;
    }

    /**
     * Represents the number of years
     *
     * This accessor provides access to the private {@link _years} property, allowing the retrieval and
     * update of the years value. When the value is updated, the {@link notifyChange} method is
     * automatically invoked to propagate changes.
     *
     * @return the number of years
     * @protected
     * @see notifyChange
     */
    protected get years(): number {
        return this._years;
    }

    /**
     * Sets the number of years and triggers a change notification
     *
     * Updates the {@link _years} property with the provided value and invokes the {@link notifyChange} method
     * to propagate the change.
     *
     * @param value the new value for the years' property.
     * @protected
     * @see notifyChange
     */
    protected set years(value: number) {
        this._years = value;
        this.notifyChange();
    }

    /**
     * Represents the number of months
     *
     * This accessor provides access to the private {@link _months} property, allowing the retrieval and
     * update of the months value. When the value is updated, the {@link notifyChange} method is
     * automatically invoked to propagate changes.
     *
     * @return the number of months
     * @protected
     * @see notifyChange
     */
    protected get months(): number {
        return this._months;
    }

    /**
     * Sets the number of months and triggers a change notification
     *
     * Updates the {@link _months} property with the provided value and invokes the {@link notifyChange} method
     * to propagate the change.
     *
     * @param value the new value for the months' property.
     * @protected
     * @see notifyChange
     */
    protected set months(value: number) {
        this._months = value;
        this.notifyChange();
    }

    /**
     * Represents the number of weeks
     *
     * This accessor provides access to the private {@link _weeks} property, allowing the retrieval and
     * update of the weeks value. When the value is updated, the {@link notifyChange} method is
     * automatically invoked to propagate changes.
     *
     * @return the number of weeks
     * @protected
     * @see notifyChange
     */
    protected get weeks(): number {
        return this._weeks;
    }

    /**
     * Sets the number of weeks and triggers a change notification
     *
     * Updates the {@link _weeks} property with the provided value and invokes the {@link notifyChange} method
     * to propagate the change.
     *
     * @param value the new value for the weeks' property
     * @protected
     * @see notifyChange
     */
    protected set weeks(value: number) {
        this._weeks = value;
        this.notifyChange();
    }

    /**
     * Represents the number of days
     *
     * This accessor provides access to the private {@link _days} property, allowing the retrieval and
     * update of the days value. When the value is updated, the {@link notifyChange} method is
     * automatically invoked to propagate changes.
     *
     * @return the number of days
     * @protected
     * @see notifyChange
     */
    protected get days(): number {
        return this._days;
    }

    /**
     * Sets the number of days and triggers a change notification
     *
     * Updates the {@link _days} property with the provided value and invokes the {@link notifyChange} method
     * to propagate the change.
     *
     * @param value the new value for the days' property
     * @protected
     * @see notifyChange
     */
    protected set days(value: number) {
        this._days = value;
        this.notifyChange();
    }

    /**
     * Represents the number of hours
     *
     * This accessor provides access to the private {@link _hours} property, allowing the retrieval and
     * update of the hours value. When the value is updated, the {@link notifyChange} method is
     * automatically invoked to propagate changes.
     *
     * @return the number of hours
     * @protected
     * @see notifyChange
     */
    protected get hours(): number {
        return this._hours;
    }

    /**
     * Sets the number of hours and triggers a change notification
     *
     * Updates the {@link _hours} property with the provided value and invokes the {@link notifyChange} method
     * to propagate the change.
     *
     * @param value the new value for the hours' property
     * @protected
     * @see notifyChange
     */
    protected set hours(value: number) {
        this._hours = value;
        this.notifyChange();
    }

    /**
     * Represents the number of minutes
     *
     * This accessor provides access to the private {@code _minutes} property, allowing the retrieval and
     * update of the minutes value. When the value is updated, the {@link notifyChange} method is
     * automatically invoked to propagate changes.
     *
     * @return the number of minutes
     * @protected
     * @see notifyChange
     */
    protected get minutes(): number {
        return this._minutes;
    }

    /**
     * Sets the number of minutes and triggers a change notification
     *
     * Updates the {@code _minutes} property with the provided value and invokes the {@link notifyChange} method
     * to propagate the change.
     *
     * @param value the new value for the minutes' property.
     * @protected
     * @see notifyChange
     */
    protected set minutes(value: number) {
        this._minutes = value;
        this.notifyChange();
    }

    /**
     * Represents the number of seconds
     *
     * This accessor provides access to the private {@code _seconds} property, allowing the retrieval and
     * update of the seconds value. When the value is updated, the {@link notifyChange} method is
     * automatically invoked to propagate changes.
     *
     * @return the number of seconds
     * @protected
     * @see notifyChange
     */
    protected get seconds(): number {
        return this._seconds;
    }

    /**
     * Sets the number of seconds and triggers a change notification
     *
     * Updates the {@code _seconds} property with the provided value and invokes the {@link notifyChange} method
     * to propagate the change.
     *
     * @param value the new value for the seconds' property.
     * @protected
     * @see notifyChange
     */
    protected set seconds(value: number) {
        this._seconds = value;
        this.notifyChange();
    }

    public validate(): { invalidTime: boolean } | null {
        if(this.allowNegative())
        {
            return null;
        }

        if // I hate everything in this class
        (
            this.years < 0 ||
            this.months < 0 ||
            this.weeks < 0 ||
            this.months < 0 ||
            this.weeks < 0 ||
            this.days < 0 ||
            this.hours < 0 ||
            this.minutes < 0 ||
            this.seconds < 0
        )
        {
            return {invalidTime: true};
        }
        return null;
    }
}
