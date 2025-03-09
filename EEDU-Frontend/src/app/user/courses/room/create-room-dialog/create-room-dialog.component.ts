import {Component} from '@angular/core';
import {SimpleCreateDialogComponent} from "../../../../entity/create-entity/simple-create-dialog/simple-create-dialog.component";
import {AbstractSimpleCreateEntity} from "../../../../entity/create-entity/abstract-simple-create-entity";
import {DialogRef} from "@angular/cdk/dialog";
import {RoomService} from "../room.service";

@Component({
    imports: [SimpleCreateDialogComponent],
    template: '<app-simple-create-dialog [title]="title" (submit)="create($event)"></app-simple-create-dialog>',
})
export class CreateRoomDialogComponent extends AbstractSimpleCreateEntity {

    public constructor(service: RoomService, dialogRef: DialogRef) {
        super(service, dialogRef, 'Create Room');
    }
}
