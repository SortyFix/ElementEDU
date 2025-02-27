import {Component} from '@angular/core';
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {FormBuilder, ReactiveFormsModule} from "@angular/forms";
import {DialogRef} from "@angular/cdk/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {RoomService} from "../room.service";
import {RoomModel} from "../room-model";
import {MatChipGrid, MatChipInput, MatChipRemove, MatChipRow} from "@angular/material/chips";
import {MatIcon} from "@angular/material/icon";
import {
    AbstractCourseComponentsCreateMultiple
} from "../../abstract-course-components/create/abstract-course-components-create-multiple";
import {GeneralCardComponent} from "../../../../common/general-card-component/general-card.component";

@Component({
    selector: 'app-create-room',
    standalone: true,
    imports: [
        GeneralCardComponent,
        MatCardContent,
        MatCardActions,
        MatButton,
        MatDialogClose,
        MatLabel,
        MatFormField,
        ReactiveFormsModule,
        MatInput,
        MatChipGrid,
        MatChipRow,
        MatIcon,
        MatChipInput,
        MatChipRemove
    ],
    templateUrl: '../../abstract-course-components/create/abstract-course-components-create-multiple.html',
})
export class CreateRoomComponent extends AbstractCourseComponentsCreateMultiple<RoomModel> {

    public constructor(roomService: RoomService, dialogRef: DialogRef, formBuilder: FormBuilder) {
        super(roomService, dialogRef, formBuilder, "Create Room");
    }
}
