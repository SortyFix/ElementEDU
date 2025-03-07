import {Component, Inject} from '@angular/core';
import {RoomModel} from "../room-model";
import {RoomService} from "../room.service";
import {MatIcon} from "@angular/material/icon";
import {MAT_DIALOG_DATA, MatDialogClose, MatDialogRef} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import {DeleteDialogComponent} from "../../../../common/delete-dialog/delete-dialog.component";
import {AbstractDeleteDialog} from "../../abstract-course-components/delete/abstract-delete-dialog";
import {
    AbstractCourseComponentsCreateMultiple
} from "../../abstract-course-components/create/abstract-course-components-create-multiple";
import {MatChipGrid, MatChipInput, MatChipRemove, MatChipRow} from "@angular/material/chips";
import {MatInput} from "@angular/material/input";
import {FormBuilder, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {GeneralCardComponent} from "../../../../common/general-card-component/general-card.component";
import {DialogRef} from "@angular/cdk/dialog";

@Component({
    imports: [GeneralCardComponent, MatCardContent, MatCardActions, MatButton, MatDialogClose, MatLabel, MatFormField, ReactiveFormsModule, MatInput, MatChipGrid, MatChipRow, MatIcon, MatChipInput, MatChipRemove],
    templateUrl: '../../abstract-course-components/create/abstract-course-components-create-multiple.html',
})
export class CreateRoomComponent extends AbstractCourseComponentsCreateMultiple<RoomModel> {

    public constructor(roomService: RoomService, dialogRef: DialogRef, formBuilder: FormBuilder) {
        super(roomService, dialogRef, formBuilder, "Create Room");
    }
}

@Component({
    imports: [DeleteDialogComponent],
    template: '<app-delete-dialog title="room(s)" [entries]="entries" [ref]="ref"></app-delete-dialog>'
})
export class DeleteRoomComponent extends AbstractDeleteDialog<RoomModel> {

    public constructor(@Inject(MAT_DIALOG_DATA) data: {
        entries: RoomModel[]
    }, ref: MatDialogRef<DeleteRoomComponent>) { super(data, ref); }

    protected override get entries(): string[] {
        return this.data.entries.map((room: RoomModel): string => room.id);
    }
}
