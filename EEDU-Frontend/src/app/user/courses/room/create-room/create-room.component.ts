import { Component } from '@angular/core';
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {DialogRef} from "@angular/cdk/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {RoomService} from "../room.service";
import {GeneralCreateComponent} from "../../../../timetable/general-create-component/general-create.component";
import {AbstractCreateComponent} from "../../abstract-create-component";
import {RoomModel} from "../room-model";

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
        MatInput
    ],
  templateUrl: './create-room.component.html',
  styleUrl: './create-room.component.scss'
})
export class CreateRoomComponent extends AbstractCreateComponent<RoomModel> {

    public constructor(roomService: RoomService, dialogRef: DialogRef, formBuilder: FormBuilder)
    {
        super(roomService, dialogRef, formBuilder);
    }
}
