import {Component, input, InputSignal} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {NgForOf, NgIf} from "@angular/common";
import {MatCheckbox} from "@angular/material/checkbox";
import {
    MatList,
    MatListItem,
    MatListItemIcon,
    MatListItemTitle,
} from "@angular/material/list";
import {
    MatAccordion,
    MatExpansionPanel, MatExpansionPanelDescription,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle
} from "@angular/material/expansion";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {FormsModule} from "@angular/forms";
import {MatInput} from "@angular/material/input";

export enum SelectionType
{
    MULTIPLE,
    SINGLE,
    NONE
}

@Component({
  selector: 'list',
    imports: [
        MatIcon,
        NgIf,
        MatCheckbox,
        MatListItemTitle,
        MatExpansionPanelTitle,
        MatExpansionPanelHeader,
        MatExpansionPanelDescription,
        MatChip,
        MatChipSet,
        MatExpansionPanel,
        NgForOf,
        MatAccordion,
        MatLabel,
        MatFormField,
        MatListItemTitle,
        MatList,
        FormsModule,
        MatInput,
        MatListItemIcon,
        MatListItem
    ],
  templateUrl: './abstract-list.component.html',
  styleUrl: './abstract-list.component.scss'
})
export class AbstractList<T> {

    public readonly title: InputSignal<(value: T) => string> = input<(value: T) => string>((): string => "title");
    public readonly icon: InputSignal<((value: T) => string) | undefined> = input<((value: T) => string) | undefined>(undefined);
    public readonly chips: InputSignal<((value: T) => string[]) | undefined> = input<((value: T) => string[]) | undefined>(undefined);

    public readonly filter: InputSignal<((input: string, values: T[]) => T[])> = input<((input: string, values: T[]) => T[])>((): T[] => this.values());

    public readonly values: InputSignal<T[]> = input<T[]>([]);

    public readonly selectionType: InputSignal<SelectionType> = input<SelectionType>(SelectionType.SINGLE);

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

    protected get hasIcon(): boolean
    {
        return !!this.icon();
    }

    protected loadIcon(value: T): string
    {
        return this.icon()!(value);
    }

    protected get hasChips(): boolean
    {
        return !!this.icon();
    }

    protected loadChips(value: T): string[]
    {
        return this.chips()!(value);
    }

    protected loadTitle(value: T): string
    {
        return this.title()!(value);
    }

    protected get partiallySelected(): boolean {
        return this.selected.size > 0 && !this.isSelected('all');
    }

    protected get filteredValues(): T[]
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

    protected readonly SelectionType: typeof SelectionType = SelectionType;
}
