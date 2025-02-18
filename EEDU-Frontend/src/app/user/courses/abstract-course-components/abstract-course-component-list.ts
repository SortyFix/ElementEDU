import {AbstractCourseComponentsService} from "./abstract-course-components-service";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {ComponentType} from "@angular/cdk/overlay";
import {SelectionType} from "../../../common/abstract-list/abstract-list.component";

export abstract class AbstractCourseComponentList<T> {

    private _values: readonly T[] = [];

    protected readonly SelectionType: typeof SelectionType = SelectionType;

    protected constructor(
        private readonly _service: AbstractCourseComponentsService<T, any>,
        private readonly _dialog: MatDialog,
        private readonly _componentType: ComponentType<any>)
    {
        this.subscribe();
    }

    protected openDialog(): MatDialogRef<any>
    {
        return this._dialog.open(this._componentType, { width: '600px', disableClose: true });
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

    protected get service(): AbstractCourseComponentsService<T, any> {
        return this._service;
    }

    protected abstract title(value: T): string;

    protected icon(value: T): string { return ''; }
    protected chips(value: T): string[] { return []; }
}
