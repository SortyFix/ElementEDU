import {
    Component, computed,
    forwardRef, Input,
    input,
    InputSignal,
    model,
    ModelSignal,
    Signal,
    signal,
    Type,
    WritableSignal
} from '@angular/core';
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {
    MatAutocomplete,
    MatAutocompleteSelectedEvent,
    MatAutocompleteTrigger,
    MatOption
} from "@angular/material/autocomplete";
import {MatInput} from "@angular/material/input";
import {NgIf} from "@angular/common";
import {ControlValueAccessor, FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR, Validator} from "@angular/forms";
import {MatChipGrid, MatChipInput, MatChipInputEvent, MatChipRemove, MatChipRow} from "@angular/material/chips";
import {MatIcon} from "@angular/material/icon";
import {ENTER} from "@angular/cdk/keycodes";

const type: Type<SelectionInput<any>> = forwardRef((): typeof SelectionInput => SelectionInput);

/**
 * A reusable general selection input component
 *
 * It allows users to select one or multiple item(s) from a list of values, supports Angular forms integration through
 * the {@link ControlValueAccessor} interface, and validates the selected input via the {@link Validator} interface.
 *
 * This generic component operates on items of type T, where T must have a name property.
 *
 * @typeParam T the type of items managed by the component. Must include a name property of type string.
 *
 * @example
 * <!-- bind it to a standalone input -->
 * <general-selection-input
 *     label="Select an Option"
 *     placeholder="Option Name..."
 *     [(ngModel)]="selectedOption"
 *     [values]="availableOptions" // must include "name: string" attribute
 * ></general-selection-input>
 *
 * <!-- can also be used within a form -->
 * <form>
 *      <general-selection-input
 *          label="Select an Option"
 *          placeholder="Option Name..."
 *          formControlName="option"
 *          [values]="availableOptions" // must include "name: string" attribute
 *      ></general-selection-input>
 * </form>
 *
 * <!-- is able to capture multiple entries -->
 * <form>
 *      <general-selection-input
 *          label="Select Options"
 *          placeholder="Option Name..."
 *          formControlName="option"
 *          [values]="availableOptions" // must include "name: string" attribute
 *          [multiple]="true"
 *      ></general-selection-input>
 * </form>
 *
 * @author Ivo Quiring
 */
@Component({
    selector: 'general-selection-input',
    standalone: true,
    imports: [
        MatFormField,
        MatAutocompleteTrigger,
        MatInput,
        MatAutocomplete,
        MatOption,
        FormsModule,
        MatLabel,
        NgIf,
        MatChipGrid,
        MatChipRow,
        MatIcon,
        MatChipInput,
        MatChipRemove
    ],
    providers: [
        { provide: NG_VALUE_ACCESSOR, useExisting: type, multi: true },
        { provide: NG_VALIDATORS, useExisting: type, multi: true }
    ],
    templateUrl: './selection-input.component.html',
    styleUrl: './selection-input.component.scss'
})
export class SelectionInput<T extends {name: string} | { id: string }> implements ControlValueAccessor, Validator {

    protected readonly ENTER: 13 = ENTER;

    public label: InputSignal<string | null> = input<string | null>(null);
    public placeholder: InputSignal<string> = input<string>('');

    public allowNull: InputSignal<boolean> = input<boolean>(false);

    public allowDuplicates: InputSignal<boolean> = input<boolean>(false);
    public multiple: InputSignal<boolean> = input<boolean>(false);

    protected currentValue: ModelSignal<string> = model<string>('');
    private _values: WritableSignal<readonly T[]> = signal<readonly T[]>([]);
    protected selectedValues: WritableSignal<readonly T[]> = signal<readonly T[]>([]);

    @Input() public set values(values: readonly T[]) { this._values.update((): readonly T[] => values); }

    /**
     * Returns all accessible values from the list of values.
     *
     * Returns an array of values accessible to the user to select.
     * This needs to be done, because when not allowing duplicates the accessible
     * values need to dynamically disappear accordingly.
     *
     * @protected
     */
    protected accessibleValues: Signal<T[]> = computed((): T[] => {
        return this._values().filter((value: T): boolean =>
        {
            return this.allowDuplicates() || !this.selectedValues().includes(value)
        });
    });

    /**
     * Filters values based on the input given.
     *
     * Returns an array of filtered values retrieved from {@link accessibleValues} consisting of the type T provided by the class.
     * This is required for live updating the suggestions while the user enters something into
     * the input field.
     *
     * When the current value is undefined it will not filter anything.
     *
     * @protected
     */
    protected filteredValues: Signal<readonly T[]> = computed((): readonly T[] => {
        const currentValue: string | undefined = this.currentValue()?.toLowerCase();
        if(!currentValue) {
            return this.accessibleValues().slice();
        }

        return this.accessibleValues().filter((value: T): boolean =>
        {
            return this.toName(value).toLowerCase().includes(currentValue);
        });
    });

    protected toName(value: T): string
    {
        if('name' in value && typeof value.name === 'string')
        {
            return value.name;
        }

        if('id' in value && typeof value.id === 'string')
        {
            return value.id;
        }

        throw new Error("Unknown value name");
    }

    public onChange: (value: readonly T[] | T) => void = (): void => {};
    public onTouched: () => void = (): void => {};

    /**
     * Writes a new value to the component
     *
     * This method sets the internal value of the component to the provided value. It is part of the
     * {@link ControlValueAccessor} interface implementation and is invoked to update the value programmatically.
     *
     * @param value the new value of type T or null to be written to the component.
     * @public
     */
    public writeValue(value: T): void {

        if(!value) {
            return;
        }

        this.currentValue.set(this.toName(value));
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
        this.onChange = fn;
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
     * Validates the current value of the component.
     *
     * This method checks whether the component's value can be considered valid based on its configuration.
     * If multiple values are allowed, the component is considered
     * invalid when the length of the {@link selectedValues()} array is 0.
     *
     * If only one value is allowed, the component is considered invalid when the current value does not
     * match any value from the {@link accessibleValues()} array.
     *
     * @returns {{ invalidSelection: true } | { unset: true } | null }
     * { invalidSelection: true } is returned when multiple values are allowed, but none have been selected.
     * { unset: true } is returned when only one value is allowed, but none has been selected.
     * null is returned when it is acceptable to have no value selected, or when the component's configuration requirements are met.
     *
     * @public
     */
    public validate(): { invalidSelection: true } | { unset: true } | null {

        if(this.allowNull())
        {
            return null;
        }

        if(this.multiple() && this.selectedValues().length == 0)
        {
            return { invalidSelection: true };
        }

        if(!this.multiple() && this.filter(this.currentValue()).length !== 1)
        {
            return { unset: true };
        }

        return null;
    }

    protected add(event: MatChipInputEvent): string {

        const value: T[] = this.filter((event.value || '').trim());
        if(value.length != 1)
        {
            return event.value;
        }

        this.value = value[0];
        return '';
    }

    protected selected(event: MatAutocompleteSelectedEvent): void {
        const value: T[] = this.filter(event.option.viewValue);
        if(value.length == 1)
        {
            this.value = value[0];
        }

        event.option.deselect();
    }

    private filter(input: string): T[]
    {
        const loweredInput: string = input.toLowerCase();
        return this.accessibleValues().filter((current: T): boolean =>
        {
            const loweredCurrent: string = this.toName(current).toLowerCase();
            return loweredCurrent.includes(loweredInput);
        });
    }

    protected set value(value: T)
    {
        if(this.multiple())
        {
            this.selectedValues.update((values: readonly T[]): readonly T[] => [...values, value]);
            this.onChange(this.selectedValues())
            this.currentValue.set('');
            return;
        }

        this.currentValue.set(this.toName(value));
        this.onChange(value);
    }

    protected remove(value: T): void {
        this.selectedValues.update((values: readonly T[]): readonly T[] => {
            const index: number = values.indexOf(value);
            if (index < 0) {
                return values;
            }

            const updatedValues: T[] = values.filter((_: T, i: number): boolean => i !== index);
            this.onChange(updatedValues);
            return updatedValues;
        });
    }
}
