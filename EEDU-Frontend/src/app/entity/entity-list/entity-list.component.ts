import {AfterViewInit, ChangeDetectorRef, Component, EventEmitter, input, InputSignal, Output} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {AbstractList, ListItemInfo, SelectionType} from "../../common/abstract-list/abstract-list.component";
import {NgIf} from "@angular/common";
import {MatProgressBar} from "@angular/material/progress-bar";
import {MatButton, MatIconButton} from "@angular/material/button";
import {EntityService} from "../entity-service";
import {MatDialog} from "@angular/material/dialog";
import {ComponentType} from "@angular/cdk/overlay";

@Component({
    selector: 'app-entity-list',
    imports: [MatIcon, AbstractList, NgIf, MatProgressBar, MatIconButton, MatButton],
    templateUrl: './entity-list.component.html',
    styleUrl: './entity-list.component.scss'
})
export class EntityListComponent<T extends { id: any }> implements AfterViewInit {

    @Output() public readonly deletePressed: EventEmitter<T[]> = new EventEmitter<T[]>;
    public readonly service: InputSignal<EntityService<any, T, any, any> | null> = input<EntityService<any, T, any, any> | null>(null)
    public readonly itemInfo: InputSignal<ListItemInfo<T> | null> = input<ListItemInfo<T> | null>(null);
    protected readonly SelectionType: typeof SelectionType = SelectionType;

    public constructor(private readonly _matDialog: MatDialog, private readonly cdr: ChangeDetectorRef) {}

    private _values: readonly T[] = [];

    protected get values(): readonly T[] {
        return this._values;
    }

    protected get loaded(): boolean {
        return this.service()?.fetched || false;
    }

    public ngAfterViewInit(): void {
        if (!this.service()) {
            return;
        }
        this.service()?.value$.subscribe((value: T[]): void => {
            this._values = value;
            this.cdr.detectChanges();
        })
    }

    public openCreateDialog(): void {
        this._matDialog.open(this.service()?.createDialogType as ComponentType<T>, {width: '600px'});
    }
}
