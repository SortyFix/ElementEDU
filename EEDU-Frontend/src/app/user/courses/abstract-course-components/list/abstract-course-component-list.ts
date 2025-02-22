import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {ComponentType} from "@angular/cdk/overlay";
import {ListItemInfo, SelectionType} from "../../../../common/abstract-list/abstract-list.component";
import {AbstractCourseComponentsService} from "../abstract-course-components-service";
import {ListItemContent} from "../../../../common/abstract-list/list-item-content";
import {Type} from "@angular/core";

export abstract class AbstractCourseComponentList<P, T extends { id: P }> {

    private _values: readonly T[] = [];

    protected readonly SelectionType: typeof SelectionType = SelectionType;

    protected constructor(
        private readonly _service: AbstractCourseComponentsService<P, T, any>,
        private readonly _dialog: MatDialog,
        private readonly _componentType: ComponentType<any>,
        private readonly _listData: ListItemInfo<T>)
    {

        if(!this._listData.icon)
        {
            this._listData.icon = (): string => _service.icon;
        }

        this.subscribe();
    }

    protected get content(): Type<ListItemContent<T>> | null { return null; }

    protected openDialog(): MatDialogRef<any>
    {
        return this._dialog.open(this._componentType, { width: '600px', disableClose: true });
    }

    protected openDeleteDialog(): MatDialogRef<any>
    {
        return this._dialog.open(this._componentType, { width: '600px', disableClose: true });
    }

    protected delete(selectedValues: T[]): void
    {
        this._service.delete(selectedValues.map((value: T): P => value.id)).subscribe();
    }

    protected subscribe(): void
    {
        this._service.value$.subscribe((value: T[]): void => { this._values = value; });
    }

    protected get values(): readonly T[] {
        return this._values;
    }

    protected set values(value: readonly T[]) {
        this._values = value;
    }

    protected get loaded(): boolean
    {
        return this.service.fetched;
    }

    protected get service(): AbstractCourseComponentsService<P, T, any> {
        return this._service;
    }

    protected get itemInfo(): ListItemInfo<T> {
        return this._listData;
    }
}
