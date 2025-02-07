import {
    Component, computed,
    forwardRef,
    input,
    InputSignal,
    model,
    ModelSignal,
    OnChanges, Signal,
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

const type: Type<GeneralMultipleSelectionInput<any>> = forwardRef((): typeof GeneralMultipleSelectionInput => GeneralMultipleSelectionInput);

@Component({
    selector: 'app-general-multiple-selection-input',
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
    templateUrl: './general-multiple-selection-input.component.html',
    styleUrl: './general-multiple-selection-input.component.scss'
})
export class GeneralMultipleSelectionInput<T extends {name: string}> implements ControlValueAccessor, Validator {

    public label: InputSignal<string | null> = input<string | null>(null);
    public placeholder: InputSignal<string> = input<string>('');
    public values: InputSignal<T[]> = input<T[]>([]);
    public allowDuplicates: InputSignal<boolean> = input<boolean>(false);

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


    public onChange: (value: T[]) => void = (): void => {};
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

    public validate(): { invalidSelection: boolean } | null {
        if(this.selectedValues().length == 0)
        {
            return { invalidSelection: true };
        }
        return null;
    }

    protected get separatorKeysCodes(): number[] {
        return [ENTER];
    }

    protected add(event: MatChipInputEvent): string {

        const value: T[] = this.filter((event.value || '').trim());
        if(value.length != 1)
        {
            return event.value;
        }

        this.selectedValues.update((values: T[]): T[] => [...values, ...value]);
        this.onChange(this.selectedValues());

        this.currentValue.set('');
        return '';
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

    protected selected(event: MatAutocompleteSelectedEvent): void {
        const value: T | null = this.values().find((c: T): boolean => c.name == event.option.viewValue) || null;
        if(value)
        {
            this.selectedValues.update((values: T[]): T[] => [...values, value]);
            this.onChange(this.selectedValues())
        }

        this.currentValue.set('');
        event.option.deselect();
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
}
