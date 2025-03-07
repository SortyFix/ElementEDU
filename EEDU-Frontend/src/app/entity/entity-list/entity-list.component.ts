import {AfterViewInit, Component, EventEmitter, input, InputSignal, Output} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {AbstractList, ListItemInfo, SelectionType} from "../../common/abstract-list/abstract-list.component";
import {NgIf} from "@angular/common";
import {MatProgressBar} from "@angular/material/progress-bar";
import {MatButton, MatIconButton} from "@angular/material/button";
import {EntityService} from "../entity-service";

@Component({
  selector: 'app-entity-list',
    imports: [
        MatIcon,
        AbstractList,
        NgIf,
        MatProgressBar,
        MatButton,
        MatIconButton
    ],
  templateUrl: './entity-list.component.html',
  styleUrl: './entity-list.component.scss'
})
export class EntityListComponent<T extends { id: any }> implements AfterViewInit {

    protected readonly SelectionType: typeof SelectionType = SelectionType;
    @Output() public readonly addPressed: EventEmitter<void> = new EventEmitter<void>;
    @Output() public readonly deletePressed: EventEmitter<T[]> = new EventEmitter<T[]>;

    public readonly service: InputSignal<EntityService<any, T, any, any> | null> = input<EntityService<any, T, any, any> | null>(null)
    public readonly itemInfo: InputSignal<ListItemInfo<T> | null> = input<ListItemInfo<T> | null>(null);

    private _values: readonly T[] = [];

    public ngAfterViewInit(): void {
        if(!this.service())
        {
            return;
        }
        this.service()?.value$.subscribe((value: T[]): void => { this._values = value; })
    }

    protected get loaded(): boolean
    {
        return this.service()?.fetched || false;
    }

    protected get values(): readonly T[] {
        return this._values;
    }
}
