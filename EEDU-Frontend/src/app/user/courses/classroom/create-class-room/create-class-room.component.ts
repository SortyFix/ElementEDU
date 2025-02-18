import {Component} from '@angular/core';
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
import {AbstractCourseComponentsCreate} from "../../abstract-course-components/abstract-course-components-create";

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
})
export class CreateClassRoomComponent extends AbstractCourseComponentsCreate<ClassRoomModel> {

    private _users: ReducedUserModel[] = [];
    private _courses: CourseModel[] = [];

    constructor(service: ClassRoomService, dialogRef: DialogRef, formBuilder: FormBuilder, userService: UserService, private readonly _courseService: CourseService) {
        super(service, dialogRef, formBuilder, "Create Class Room");

        userService.fetchAllReduced.subscribe((user: ReducedUserModel[]): void => { this._users = user; });
        this._courseService.adminCourses$.subscribe((course: CourseModel[]): void => { this._courses = course; });
    }

    protected override get loading(): boolean {
        return super.loading && this._courseService.fetchedAdmin;
    }

    protected override getForm(formBuilder: FormBuilder): FormGroup {
        return formBuilder.group({
            name: [null, Validators.required],
            students: [null, Validators.required],
            courses: [null, Validators.required],
            tutor: [null, Validators.required],
        });
    }

    protected override get createModel(): ClassRoomCreateModel[]
    {
        return [ClassRoomCreateModel.fromObject(this.form.value)];
    }

    protected get teacher(): ReducedUserModel[] { return this.getUsers(AccountType.TEACHER); }

    protected get students(): ReducedUserModel[] { return this.getUsers(AccountType.STUDENT); }

    protected get courses(): CourseModel[] { return this._courses; }

    private getUsers(accountType: AccountType): ReducedUserModel[] {
        return this._users.filter((user: ReducedUserModel): boolean => user.accountType === accountType);
    }
}
