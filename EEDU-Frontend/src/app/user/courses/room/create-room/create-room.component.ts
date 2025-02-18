import { Component } from '@angular/core';
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {FormBuilder, ReactiveFormsModule} from "@angular/forms";
import {DialogRef} from "@angular/cdk/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {RoomService} from "../room.service";
import {GeneralCreateComponent} from "../../../../timetable/general-create-component/general-create.component";
import {RoomModel} from "../room-model";
import {
    AbstractCourseComponentsCreateMultiple
} from "../../abstract-course-components/abstract-course-components-create-multiple";
import {MatChipGrid, MatChipInput, MatChipRow} from "@angular/material/chips";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-create-room',
  standalone: true,
    imports: [
        GeneralCreateComponent,
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
        MatChipInput
    ],
  templateUrl: '../../abstract-course-components/abstract-course-components-create-multiple.html',
})
export class CreateRoomComponent extends AbstractCourseComponentsCreateMultiple<RoomModel> {

    public constructor(roomService: RoomService, dialogRef: DialogRef, formBuilder: FormBuilder)
    {
        super(roomService, dialogRef, formBuilder, "Create Room");
    }
}
