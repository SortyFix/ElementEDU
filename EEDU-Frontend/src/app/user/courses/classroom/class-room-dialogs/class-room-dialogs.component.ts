import {Component, Inject} from '@angular/core';
import {ClassRoomModel} from "../class-room-model";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {DeleteDialogComponent} from "../../../../common/delete-dialog/delete-dialog.component";
import {AbstractDeleteDialog} from "../../abstract-course-components/delete/abstract-delete-dialog";

@Component({
    imports: [DeleteDialogComponent],
    template: '<app-delete-dialog title="classes(s)" [entries]="entries" [ref]="ref"></app-delete-dialog>'
})
export class DeleteClassRoomComponent extends AbstractDeleteDialog<ClassRoomModel> {

    public constructor(@Inject(MAT_DIALOG_DATA) data: {
        entries: ClassRoomModel[]
    }, ref: MatDialogRef<DeleteClassRoomComponent>) { super(data, ref); }

    protected override get entries(): string[] {
        return this.data.entries.map((classroom: ClassRoomModel): string => classroom.id);
    }
}

