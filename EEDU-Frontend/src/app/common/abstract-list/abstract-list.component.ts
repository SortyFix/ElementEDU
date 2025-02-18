import {Component, input, InputSignal} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelDescription,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle
} from "@angular/material/expansion";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {FormsModule} from "@angular/forms";
import {MatInput} from "@angular/material/input";
import {AllCheckBoxComponent} from "./checkboxes/all-check-box.component";
import {SingleCheckBoxComponent} from "./checkboxes/single-check-box.component";

// noinspection JSUnusedGlobalSymbols
export enum SelectionType
{
    MULTIPLE,
    SINGLE,
    NONE
}

export interface ListItemInfo<T>
{
    title: (value: T) => string;
    icon?: (value: T) => string;
    chips?: (value: T) => string[];
}

@Component({
    selector: 'list',
    imports: [
        MatChipSet,
        MatChip,
        MatExpansionPanel,
        MatAccordion,
        MatExpansionPanelTitle,
        MatExpansionPanelDescription,
        MatExpansionPanelHeader,
        MatFormField,
        MatInput,
        MatLabel,
        NgIf,
        FormsModule,
        NgForOf,
        AllCheckBoxComponent,
        SingleCheckBoxComponent,
    ],
    templateUrl: './abstract-list.component.html',
    styleUrl: './abstract-list.component.scss'
})
export class AbstractList<T> {

    public readonly itemInfo: InputSignal<ListItemInfo<T> | undefined> = input<ListItemInfo<T>>();

    public readonly filter: InputSignal<((input: string, values: readonly T[]) => readonly T[])> = input<((input: string, values: readonly T[]) => readonly T[])>((): readonly T[] => this.values());
    public readonly values: InputSignal<readonly T[]> = input<readonly T[]>([]);
    public readonly selectionType: InputSignal<SelectionType> = input<SelectionType>(SelectionType.SINGLE);

    public readonly height: InputSignal<number | undefined> = input<number | undefined>();

    private readonly _selected: Set<T> = new Set<T>();
    protected filteredString: string = '';

    protected handleKeyDown(event: KeyboardEvent, value: T): void {
        // noinspection FallThroughInSwitchStatementJS
        switch (event.key) { // fall through
            // @ts-ignore
            case ' ':
                this.toggle(value)
            case 'ArrowLeft':
            case 'ArrowRight':
            case "ArrowUp":
            case 'ArrowDown':
                event.stopPropagation();
                event.preventDefault();
        }
    }

    protected loadTitle(value: T): string { return this.itemInfo()!.title(value); }

    protected loadIcon(value: T): string | undefined
    {
        return this.itemInfo()?.icon ? this.itemInfo()!.icon!(value) : undefined;
    }

    protected get hasChips(): boolean { return !!this.itemInfo()!.chips; }

    protected loadChips(value: T): string[] { return this.itemInfo()!.chips!(value); }

    protected get partiallySelected(): boolean {
        return this.selected.size > 0 && !this.isSelected('all');
    }

    protected get filteredValues(): readonly T[]
    {
        return this.filter()!(this.filteredString, this.values())
    }

    public isSelected(value: T | 'all'): boolean
    {
        if (value === 'all') {
            const valueLength: number = this.filteredValues.length;
            return valueLength != 0 && this.selected.size === valueLength;
        }

        return this.selected.has(value);
    }

    protected unselectAll(): void
    {
        this.selected.clear();
    }

    public toggle(value: T | 'all'): void
    {
        if (value === 'all') {
            if (this.isSelected('all')) {
                this.unselectAll();
                return;
            }

            this.filteredValues.forEach((item: T): Set<T> => this.selected.add(item));
            return;
        }

        if(this.isSelected(value))
        {
            this._selected.delete(value);
            return;
        }
        this._selected.add(value);
    }

    private get selected(): Set<T> {
        return this._selected;
    }

    public get currentSelected(): T[]
    {
        return Array.from(this.selected.values());
    }
}
