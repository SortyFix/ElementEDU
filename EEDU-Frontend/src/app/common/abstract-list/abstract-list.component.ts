import {Component, Input, input, InputSignal, Type} from '@angular/core';
import {NgComponentOutlet, NgForOf, NgIf, NgTemplateOutlet} from "@angular/common";
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
import {ListItemContent} from "./list-item-content";
import {MatList, MatListItem} from "@angular/material/list";
import {AccessibilityService} from "../../accessibility.service";

// noinspection JSUnusedGlobalSymbols
export enum SelectionType {
    MULTIPLE, SINGLE, NONE
}

export interface ListItemInfo<T> {
    title: (value: T) => string;
    icon?: (value: T) => string;
    chips?: (value: T) => string[];
    content?: Type<ListItemContent<T>>;
}

export interface GeneralListInfo<T> {
    filter: ((input: string, values: readonly T[]) => readonly T[]);
}

@Component({
    selector: 'list',
    imports: [MatChipSet, MatChip, MatExpansionPanel, MatAccordion, MatExpansionPanelTitle, MatExpansionPanelDescription, MatExpansionPanelHeader, MatFormField, MatInput, MatLabel, NgIf, FormsModule, NgForOf, AllCheckBoxComponent, SingleCheckBoxComponent, NgComponentOutlet, MatList, MatListItem, NgTemplateOutlet,],
    templateUrl: './abstract-list.component.html',
    styleUrl: './abstract-list.component.scss'
})
export class AbstractList<T> {

    public readonly itemInfo: InputSignal<ListItemInfo<T> | null> = input<ListItemInfo<T> | null>(null);

    public readonly generalListInfo: InputSignal<GeneralListInfo<T> | null> = input<GeneralListInfo<T> | null>(null);
    public readonly selectionType: InputSignal<SelectionType> = input<SelectionType>(SelectionType.SINGLE);
    public readonly height: InputSignal<number | undefined> = input<number | undefined>();
    protected filteredString: string = '';
    private readonly _selection: Set<T> = new Set<T>();

    private _values: readonly T[] = [];

    public constructor(private readonly _accessibilityService: AccessibilityService) {}

    @Input() public set values(value: readonly T[]) {
        this._values = value as readonly T[];

        for (const item of this.selection) {
            if (value.includes(item)) {
                continue;
            }

            this.selection.delete(item);
        }
    }

    public get selected(): T[] {
        return Array.from(this.selection.values());
    }

    protected get valueArray(): readonly T[] {
        return this._values;
    }

    protected get hasChips(): boolean
    {
        return !this._accessibilityService.mobile && !!this.itemInfo()!.chips;
    }

    protected get hasContent(): boolean {
        return !!this.itemInfo()?.content;
    }

    protected get content(): Type<any> {
        return this.itemInfo()!.content!;
    }

    protected get partiallySelected(): boolean {
        return this.selection.size > 0 && !this.isSelected('all');
    }

    protected get filteredValues(): readonly T[] {
        if (!this.generalListInfo()) {
            return this.valueArray;
        }

        return this.generalListInfo()!.filter(this.filteredString, this.valueArray);
    }

    private get selection(): Set<T> {
        return this._selection;
    }

    public isSelected(value: T | 'all'): boolean {
        if (value === 'all') {
            const valueLength: number = this.filteredValues.length;
            return valueLength != 0 && this.selection.size === valueLength;
        }

        return this.selection.has(value);
    }

    public toggle(value: T | 'all'): void {
        if (value === 'all') {
            if (this.isSelected('all')) {
                this.unselectAll();
                return;
            }

            this.filteredValues.forEach((item: T): Set<T> => this._selection.add(item));
            return;
        }

        if (this.isSelected(value)) {
            this._selection.delete(value);
            return;
        }
        this._selection.add(value);
    }

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

    protected loadIcon(value: T): string | undefined {
        return this.itemInfo()?.icon ? this.itemInfo()!.icon!(value) : undefined;
    }

    protected loadChips(value: T): string[] {
        return this.itemInfo()!.chips!(value);
    }

    protected unselectAll(): void {
        this._selection.clear();
    }

    protected readonly SelectionType = SelectionType;
}
