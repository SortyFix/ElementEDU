import {Component} from '@angular/core';
import {AbstractCreateComponent} from "../../abstract-create-component";
import {ClassRoomModel} from "../class-room-model";
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {FormBuilder, ReactiveFormsModule} from "@angular/forms";
import {MatInput} from "@angular/material/input";
import {GeneralCreateComponent} from "../../../../timetable/general-create-component/general-create.component";
import {GeneralSelectionInput} from "../../../../timetable/general-selection-input/general-selection-input.component";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {DialogRef} from "@angular/cdk/dialog";
import {ClassRoomService} from "../class-room.service";
import {UserService} from "../../../user.service";
import {ReducedUserModel} from "../../../reduced-user-model";

@Component({
    selector: 'app-create-class-room',
    imports: [
        MatCardActions,
        MatButton,
        MatDialogClose,
        ReactiveFormsModule,
        MatInput,
        MatLabel,
        MatFormField,
        MatCardContent,
        GeneralCreateComponent,
        GeneralSelectionInput
    ],
    templateUrl: './create-class-room.component.html',
    styleUrl: './create-class-room.component.scss'
})
export class CreateClassRoomComponent extends AbstractCreateComponent<ClassRoomModel> {

    private readonly _users: ReducedUserModel[] = [];

    constructor(service: ClassRoomService, dialogRef: DialogRef, formBuilder: FormBuilder, userService: UserService) {
        super(service, dialogRef, formBuilder);

        userService.fetchAllReduced.subscribe((user: ReducedUserModel[]): void => {
            this._users.length = 0;
            this._users.push(...user);
        });
    }

    protected get users(): ReducedUserModel[] {
        return this._users;
    }
}
