import {Component, forwardRef, input, InputSignal, OnChanges, Output, Type} from '@angular/core';
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatAutocomplete, MatAutocompleteTrigger, MatOption} from "@angular/material/autocomplete";
import {MatInput} from "@angular/material/input";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {BehaviorSubject, combineLatest, map, Observable} from "rxjs";
import {ControlValueAccessor, FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR, Validator} from "@angular/forms";

const type: Type<GeneralSelectionInput<any>> = forwardRef((): typeof GeneralSelectionInput => GeneralSelectionInput);

/**
 * A reusable general selection input component
 *
 * It allows users to select an item from a list of values, supports Angular forms integration through
 * the {@link ControlValueAccessor} interface, and validates the selected input via the {@link Validator} interface.
 *
 * This generic component operates on items of type T, where T must have a "name" property.
 *
 * @typeParam T the type of items managed by the component. Must include a "name" property of type string.
 *
 * @example
 * <!-- bind it to a standalone input -->
 * <app-general-selection-input
 *     label="Select an Option"
 *     placeholder="Option Name..."
 *     [(ngModel)]="selectedOption"
 *     [values]="availableOptions" // must include "name: string" attribute
 * ></app-general-selection-input>
 *
 * <!-- can also be used within a form -->
 * <form>
 *      <app-general-selection-input
 *          label="Select an Option"
 *          placeholder="Option Name..."
 *          formControlName="option"
 *          [values]="availableOptions" // must include "name: string" attribute
 *      ></app-general-selection-input>
 * </form>
 *
 * @author Ivo Quiring
 */
@Component({
    selector: 'app-general-selection-input',
    standalone: true,
    imports: [
        MatFormField,
        MatAutocompleteTrigger,
        MatInput,
        MatAutocomplete,
        MatOption,
        NgForOf,
        FormsModule,
        AsyncPipe,
        MatLabel,
        NgIf
    ],
    providers: [
        { provide: NG_VALUE_ACCESSOR, useExisting: type, multi: true },
        { provide: NG_VALIDATORS, useExisting: type, multi: true }
    ],
    templateUrl: './general-selection-input.component.html',
    styleUrl: './general-selection-input.component.scss'
})
export class GeneralSelectionInput<T extends {name: string}> implements ControlValueAccessor, Validator, OnChanges {

    public label: InputSignal<string | null> = input<string | null>(null);
    public placeholder: InputSignal<string> = input<string>('');
    public values: InputSignal<T[]> = input<T[]>([]);
    public allowNull: InputSignal<boolean> = input<boolean>(false);

    private inputSubject: BehaviorSubject<string> = new BehaviorSubject<string>(''); // Track user input
    private valuesSubject: BehaviorSubject<T[]> = new BehaviorSubject<T[]>([]); // Track available values
    private readonly _filteredValues!: Observable<T[]>;

    private _value!: T | null;
    private _onChange: (value: T | null) => void = (): void => {};
    private _onTouched: () => void = (): void => {};

    /**
     * Initializes the component and sets up the filtered values observable
     *
     * The constructor creates an {@link Observable} for {@link _filteredValues} that emits filtered
     * results based on the input value.
     *
     * @public
     */
    constructor() {
        this._filteredValues = combineLatest([
            this.inputSubject.asObservable(),
            this.valuesSubject.asObservable()
        ]).pipe(
            map(([inputValue, values]) => this.filterValues(inputValue, values))
        );
    }

    /**
     * Updates the values when the input changes
     */
    public ngOnChanges(): void {
        this.valuesSubject.next(this.values());
    }

    /**
     * Filters the available values based on the input string
     *
     * This private method compares the provided input string against the names of the available values.
     * It performs a case-insensitive match and returns an array of values that include the input string.
     *
     * @param inputValue the input string to filter values against.
     * @param values the available values which can be set.
     * @returns an array of values of type T that match the filter criteria.
     * @private
     */
    private filterValues(inputValue: string, values: T[]): T[] {
        const filterValue: string = inputValue.toLowerCase();
        return values.filter((value: T) => value.name.toLowerCase().includes(filterValue));
    }

    /**
     * Handles input changes and updates the component's value accordingly
     *
     * This method is triggered when the input value changes. It attempts to find a matching value
     * from the available values and updates the internal value ({@link _value}) and invokes the registered
     * onChange callback with the new value.
     *
     * @param value the input string entered by the user.
     * @public
     */
    public onInputChange(value: string): void {
        this.inputSubject.next(value);

        const matchedValue: T | null = this.values().find((item: T) => item.name === value) || null;
        this._value = matchedValue;
        this._onChange(matchedValue);
    }

    /**
     * Writes a new value to the component
     *
     * This method sets the internal value of the component to the provided value. It is part of the
     * {@link ControlValueAccessor} interface implementation and is invoked to update the value programmatically.
     *
     * @param value the new value of type T or null to be written to the component.
     * @public
     */
    public writeValue(value: T | null): void {
        this._value = value;
        this.inputSubject.next(value?.name || ''); // Sync input value
    }

    /**
     * Registers a callback function to handle changes to the component's value
     *
     * This method is part of the {@link ControlValueAccessor} interface implementation. It assigns the provided
     * callback function to be invoked whenever the value of the component changes.
     *
     * @param fn the callback function to handle value changes.
     * @public
     */
    public registerOnChange(fn: any): void {
        this._onChange = fn;
    }

    /**
     * Registers a callback function to handle the touched state of the component
     *
     * This method is part of the {@link ControlValueAccessor} interface implementation. It assigns the provided
     * callback function to be invoked when the component is marked as touched by user interaction.
     *
     * @param fn the callback function to handle the touched state.
     * @public
     */
    public registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    /**
     * Validates the current value of the component
     *
     * This method checks whether the current value of the component is valid based on its presence
     * in the available values. If valid, it returns null; otherwise, it returns an object indicating
     * the validation error.
     *
     * @returns an object with a validation error ({ invalidCourse: true }) if the value is invalid, or null if the value is valid.
     * @public
     */
    public validate(): { invalidCourse: boolean } | null {
        if (this.allowNull() && !this._value) {
            return null;
        }

        const isValid = this.values().some((item: T) => item.name === this._value?.name);
        return isValid ? null : { invalidCourse: true };
    }

    /**
     * Retrieves the current value managed by this component
     *
     * This accessor provides the current value of the component. If no value is set, it returns null.
     * The value represents the state or data being managed or displayed.
     *
     * @returns the current value of type T or null if no value is set.
     * @protected
     */
    protected get value(): T | null {
        return this._value;
    }

    /**
     * Retrieves the filtered values as an {@link Observable}
     *
     * This accessor provides an {@link Observable} of filtered values of type {@link T[]}. The filtering
     * logic is typically applied to refine a larger dataset based on user input or specific criteria.
     *
     * @returns an observable stream of filtered values of type {@link T[]}.
     * @see filterValues
     * @protected
     */
    protected get filteredValues(): Observable<T[]> {
        return this._filteredValues;
    }

    /**
     * Updates the current value managed by this component
     *
     * This setter sets a new value of type T or null. Assigning a new value updates the state
     * or data being managed by the component.
     *
     * @param value the new value of type T or null to set.
     * @protected
     */
    protected set value(value: T | null) {
        this._value = value;
    }

    /**
     * Retrieves the "onTouched" callback function
     *
     * This accessor provides the callback function to be invoked when the component is marked as "touched."
     *
     * @returns a function to be executed when the component is touched.
     * @protected
     */
    protected get onTouched(): () => void {
        return this._onTouched;
    }

    /**
     * Updates the "onTouched" callback function.
     *
     * This setter sets a new callback function to be invoked when the component is marked as "touched."
     *
     * @param value the new callback function to set.
     * @private
     */
    private set onTouched(value: () => void) {
        this._onTouched = value;
    }
}
