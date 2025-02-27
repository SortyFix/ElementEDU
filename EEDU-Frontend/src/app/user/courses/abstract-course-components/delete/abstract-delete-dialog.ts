import {MatDialogRef} from "@angular/material/dialog";

export abstract class AbstractDeleteDialog<T> {

    protected constructor(
        private readonly _data: { entries: T[] },
        private readonly _ref: MatDialogRef<any>
    ) {}

    protected get data(): { entries: T[] } {
        return this._data;
    }

    protected abstract get entries(): string[];

    protected get ref(): MatDialogRef<any> {
        return this._ref;
    }
}
