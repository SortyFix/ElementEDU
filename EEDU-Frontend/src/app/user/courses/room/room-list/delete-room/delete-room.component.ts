import {Component, Inject} from '@angular/core';
import {DeleteDialogComponent} from "../../../../../common/delete-dialog/delete-dialog.component";
import {AbstractDeleteDialog} from "../../../abstract-course-components/delete/abstract-delete-dialog";
import {RoomModel} from "../../room-model";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
    selector: 'app-delete-room',
    imports: [DeleteDialogComponent],
    template: '<app-delete-dialog title="room(s)" [entries]="entries" [ref]="ref"></app-delete-dialog>'
})
export class DeleteRoomComponent extends AbstractDeleteDialog<RoomModel> {

    public constructor(
        @Inject(MAT_DIALOG_DATA) data: { entries: RoomModel[] }, ref: MatDialogRef<DeleteRoomComponent>
    ) { super(data, ref); }

    protected override get entries(): string[] {
        return this.data.entries.map((room: RoomModel): string => room.id);
    }
}
