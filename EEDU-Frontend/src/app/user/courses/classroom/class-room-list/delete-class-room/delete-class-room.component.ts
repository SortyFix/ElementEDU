import {Component, Inject} from '@angular/core';
import {AbstractDeleteDialog} from "../../../abstract-course-components/delete/abstract-delete-dialog";
import {ClassRoomModel} from "../../class-room-model";
import {DeleteDialogComponent} from "../../../../../common/delete-dialog/delete-dialog.component";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
    selector: 'app-delete-class-room',
    imports: [DeleteDialogComponent],
    template: '<app-delete-dialog title="class(es)" [entries]="entries" [ref]="ref"></app-delete-dialog>'
})
export class DeleteClassRoomComponent extends AbstractDeleteDialog<ClassRoomModel> {

    public constructor(
        @Inject(MAT_DIALOG_DATA) data: { entries: ClassRoomModel[] }, ref: MatDialogRef<DeleteClassRoomComponent>
    ) { super(data, ref); }

    protected override get entries(): string[] {
        return this.data.entries.map((classRoomModel: ClassRoomModel): string => classRoomModel.id);
    }

}
