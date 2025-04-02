import {EntityService} from "../entity-service";
import {DialogRef} from "@angular/cdk/dialog";

export abstract class AbstractSimpleCreateEntity {
    protected constructor(private readonly _service: EntityService<any, any, any, any>, private readonly _dialogRef: DialogRef, private readonly _title: string) {}


    protected get title(): string {
        return this._title;
    }

    protected get service(): EntityService<any, any, any, any> {
        return this._service;
    }

    protected closeDialog(): void {
        this._dialogRef.close();
    }

    protected create(createModels: any[]): void {
        this.service.create(createModels).subscribe((): void => this.closeDialog());
    }
}
