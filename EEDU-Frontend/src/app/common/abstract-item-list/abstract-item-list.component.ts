import {Component, input, InputSignal} from '@angular/core';
import {MatList, MatListItem, MatListItemTitle} from "@angular/material/list";
import {NgForOf, NgIf} from "@angular/common";
import {MatCheckbox} from "@angular/material/checkbox";
import {
    MatAccordion,
    MatExpansionPanel, MatExpansionPanelDescription,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle
} from "@angular/material/expansion";
import {MatIcon} from "@angular/material/icon";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {MatTooltip} from "@angular/material/tooltip";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {FormsModule} from "@angular/forms";
import {MatInput} from "@angular/material/input";

@Component({
  selector: 'app-abstract-item-list',
    imports: [
        MatList,
        MatListItem,
        NgForOf,
        MatCheckbox,
        NgIf,
        MatExpansionPanelTitle,
        MatExpansionPanelHeader,
        MatExpansionPanel,
        MatListItemTitle,
        MatIcon,
        MatChipSet,
        MatChip,
        MatAccordion,
        MatLabel,
        FormsModule,
        MatFormField,
        MatInput,
        MatExpansionPanelDescription
    ],
  templateUrl: './abstract-item-list.component.html',
  styleUrl: './abstract-item-list.component.scss'
})
export class AbstractItemListComponent<T> {

    public readonly values: InputSignal<T[]> = input<T[]>([]);
    public title!: InputSignal<((value: T) => string)>;

    public icon: InputSignal<((value: T) => string) | undefined> = input<((value: T) => string) | undefined>(undefined);
    public chips: InputSignal<((value: T) => string[]) | undefined> = input<((value: T) => string[]) | undefined>(undefined);

    private _selected: Set<T> = new Set();
    protected filteredString: string = '';

    protected handleKeyDown(event: KeyboardEvent, value: T) {
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

    protected get partiallySelected(): boolean {
        return this.selected.size > 0 && !this.isSelected('all');
    }

    public isSelected(value: T | 'all'): boolean
    {
        if (value === 'all') {
            const valueLength: number = this.values().length;
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

            this.values().forEach((item: T): Set<T> => this.selected.add(item));
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
}
