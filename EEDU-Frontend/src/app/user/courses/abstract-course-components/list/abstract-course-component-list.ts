import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {ComponentType} from "@angular/cdk/overlay";
import {ListItemInfo, SelectionType} from "../../../../common/abstract-list/abstract-list.component";
import {ListItemContent} from "../../../../common/abstract-list/list-item-content";
import {Type} from "@angular/core";
import {EntityService} from "../../../../entity/entity-service";

export abstract class AbstractCourseComponentList<P, T extends { id: P }> {

    protected readonly SelectionType: typeof SelectionType = SelectionType;

    protected constructor(
        private readonly _service: EntityService<P, T, any, any>,
        private readonly _dialog: MatDialog,
        private readonly _componentType: ComponentType<any>,
        private readonly _deleteComponent: ComponentType<any>,
        private readonly _listData: ListItemInfo<T>
    ) {

        this.subscribe();
    }

    private _values: readonly T[] = [];

    protected get values(): readonly T[] {
        return this._values;
    }

    protected set values(value: readonly T[]) {
        this._values = value;
    }

    protected get content(): Type<ListItemContent<T>> | null { return null; }

    protected get loaded(): boolean {
        return this.service.fetched;
    }

    protected get service(): EntityService<P, T, any, any> {
        return this._service;
    }

    protected get itemInfo(): ListItemInfo<T> {
        return this._listData;
    }

    protected openDialog(): MatDialogRef<any> {
        return this._dialog.open(this._componentType, {width: '600px', disableClose: true});
    }

    protected openDeleteDialog(selectedValues: T[]): void {
        this._dialog.open(this._deleteComponent, {
            width: '600px',
            disableClose: true,
            data: { entries: selectedValues }
        }).afterClosed().subscribe(((should: boolean): void => {
            if(!should) { return; }
            this.delete(selectedValues);
        }));
    }

    private delete(selectedValues: T[]): void {
        this._service.delete(selectedValues.map((value: T): P => value.id)).subscribe();
    }

    protected subscribe(): void {
        this._service.value$.subscribe((value: T[]): void => { this._values = value; });
    }
}
