import { Component } from '@angular/core';
import {GeneralCreateComponent} from "../general-create-component/general-create.component";
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {RoomService} from "../../user/courses/room/room.service";
import {DialogRef} from "@angular/cdk/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {SubjectModel} from "../../user/courses/subject/subject-model";

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
export class CreateRoomComponent {

    private readonly _form: FormGroup;
    private _loading: boolean = true;

    public constructor(private _roomService: RoomService, private _dialogRef: DialogRef, formBuilder: FormBuilder) {
        this._form = formBuilder.group({ name: [null, Validators.required] });
        this.roomService.fetchRooms().subscribe((): void => { this.loading = false; })
    }

    protected get loading(): boolean {
        return this._loading;
    }

    private set loading(value: boolean) {
        this._loading = value;
    }

    private get dialogRef(): DialogRef {
        return this._dialogRef;
    }

    private get roomService(): RoomService {
        return this._roomService;
    }

    protected get form(): FormGroup {
        return this._form;
    }

    protected onSubmit(): void
    {
        if(this.form.invalid)
        {
            return;
        }

        this.roomService.createRoom([this.form.value]).subscribe((): void => { this.dialogRef.close(); })
    }

    protected get canSubmit(): boolean
    {
        return this.form.valid;
    }
}
