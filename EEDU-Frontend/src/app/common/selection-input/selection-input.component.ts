import {
    Component, computed,
    forwardRef,
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
export class SelectionInput<T extends {name: string}> implements ControlValueAccessor, Validator {

    public label: InputSignal<string | null> = input<string | null>(null);
    public placeholder: InputSignal<string> = input<string>('');
    public values: InputSignal<T[]> = input<T[]>([]);

    public allowNull: InputSignal<boolean> = input<boolean>(false);

    public allowDuplicates: InputSignal<boolean> = input<boolean>(false);
    public multiple: InputSignal<boolean> = input<boolean>(false);

    protected currentValue: ModelSignal<string> = model<string>('');
    protected selectedValues: WritableSignal<T[]> = signal<T[]>([]);

    protected accessibleValues: Signal<T[]> = computed((): T[] => {
        return this.values().filter((value: T): boolean =>
        {
            return this.allowDuplicates() || !this.selectedValues().includes(value)
        });
    });

    protected filteredValues: Signal<T[]> = computed((): T[] => {
        const currentValue: string | undefined = this.currentValue()?.toLowerCase();
        if(!currentValue) {
            return this.accessibleValues().slice();
        }

        return this.accessibleValues().filter((value: T): boolean =>
        {
            return value.name.toLowerCase().includes(currentValue)
        });
    });


    public onChange: (value: T[] | T) => void = (): void => {};
    public onTouched: () => void = (): void => {};

    public writeValue(value: T): void {

        if(!value) {
            return;
        }

        this.currentValue.set(value.name);
    }

    public registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    public registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    public validate(): { invalidSelection: true } | { unset: true } | null {

        if(this.multiple() && this.selectedValues().length == 0)
        {
            return { invalidSelection: true };
        }

        console.log(this.filter(this.currentValue()).length);
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
            const loweredCurrent: string = current.name.toLowerCase();
            return loweredCurrent.includes(loweredInput);
        });
    }

    protected set value(value: T)
    {
        if(this.multiple())
        {
            this.selectedValues.update((values: T[]): T[] => [...values, value]);
            this.onChange(this.selectedValues())
            this.currentValue.set('');
            return;
        }

        this.currentValue.set(value.name);
        this.onChange(value);
    }

    protected remove(value: T): void {
        this.selectedValues.update((values: T[]): T[] => {
            const index: number = values.indexOf(value);
            if (index < 0) {
                return values;
            }

            values.splice(index, 1);
            this.onChange(values);
            return [...values];
        });
    }

    protected readonly ENTER = ENTER;
}
