import {Component} from '@angular/core';
import {AbstractCreateComponent} from "../../abstract-create-component";
import {ClassRoomModel} from "../class-room-model";
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatInput} from "@angular/material/input";
import {GeneralCreateComponent} from "../../../../timetable/general-create-component/general-create.component";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {DialogRef} from "@angular/cdk/dialog";
import {ClassRoomService} from "../class-room.service";
import {UserService} from "../../../user.service";
import {ReducedUserModel} from "../../../reduced-user-model";
import {AccountType} from "../../../account-type";
import {CourseService} from "../../course.service";
import {ClassRoomCreateModel} from "../class-room-create-model";
import {SelectionInput} from "../../../../common/selection-input/selection-input.component";
import {CourseModel} from "../../course-model";

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
        SelectionInput
    ],
    templateUrl: './create-class-room.component.html',
    styleUrl: './create-class-room.component.scss'
})
export class CreateClassRoomComponent extends AbstractCreateComponent<ClassRoomModel> {

    private readonly _users: ReducedUserModel[] = [];
    private readonly _courses: CourseModel[] = [];

    constructor(service: ClassRoomService, dialogRef: DialogRef, formBuilder: FormBuilder, userService: UserService, courseService: CourseService) {
        super(service, dialogRef, formBuilder);

        userService.fetchAllReduced.subscribe((user: ReducedUserModel[]): void => {
            this._users.length = 0;
            this._users.push(...user);
        });

        courseService.value$.subscribe((course: CourseModel[]): void => {
            this._courses.length = 0;
            this._courses.push(...course);
        });
    }

    protected override getForm(formBuilder: FormBuilder): FormGroup {
        return formBuilder.group({
            name: [null, Validators.required],
            students: [null, Validators.required],
            courses: [null, Validators.required],
            tutor: [null, Validators.required],
        });
    }

    protected override get createModel(): any[] {
        return [ClassRoomCreateModel.fromObject(this.form.value)];
    }

    protected get teacher(): ReducedUserModel[] {
        return this.getUsers(AccountType.TEACHER);
    }

    protected get students(): ReducedUserModel[] {
        return this.getUsers(AccountType.STUDENT);
    }

    protected get courses(): CourseModel[] {
        return this._courses;
    }

    private getUsers(accountType: AccountType): ReducedUserModel[] {
        return this._users.filter((user: ReducedUserModel): boolean => user.accountType === accountType);
    }
}
