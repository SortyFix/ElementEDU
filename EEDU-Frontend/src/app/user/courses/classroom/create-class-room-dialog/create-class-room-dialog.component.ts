import {Component} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {GeneralCardComponent} from "../../../../common/general-card-component/general-card.component";
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {SelectionInput} from "../../../../common/selection-input/selection-input.component";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatInput} from "@angular/material/input";
import {ClassRoomService} from "../class-room.service";
import {CourseModel} from "../../course-model";
import {UserModel} from "../../../user-model";
import {AccountType} from "../../../account-type";
import {DialogRef} from "@angular/cdk/dialog";
import {UserService} from "../../../user.service";
import {CourseService} from "../../course.service";
import {AbstractCreateEntity} from "../../../../entity/create-entity/abstract-create-entity";
import {ClassRoomCreateModel} from "../class-room-create-model";

@Component({
    imports: [ReactiveFormsModule, GeneralCardComponent, MatLabel, MatCardContent, MatFormField, SelectionInput, MatCardActions, MatButton, MatDialogClose, MatInput],
    templateUrl: './create-class-room-dialog.component.html',
})
export class CreateClassRoomDialogComponent extends AbstractCreateEntity {

    private _users: readonly UserModel[] = [];
    private _courses: readonly CourseModel[] = [];

    public constructor(
        service: ClassRoomService,
        dialogRef: DialogRef,
        formBuilder: FormBuilder,
        userService: UserService,
        private readonly _courseService: CourseService
    ) {
        super(service, dialogRef, formBuilder, "Create Classroom");

        userService.fetchAll.subscribe((user: UserModel[]): void => { this._users = user; });
        this._courseService.ownCourses$.subscribe((course: CourseModel[]): void => { this._courses = course; });
    }

    protected get courses(): readonly CourseModel[] {
        return this._courses;
    }

    protected override get loading(): boolean {
        return this._courseService.fetched;
    }

    protected get teacher(): readonly UserModel[] {
        return this.getUsers(AccountType.TEACHER);
    }

    protected get students(): readonly UserModel[] {
        return this.getUsers(AccountType.STUDENT);
    }

    protected override get createModel(): ClassRoomCreateModel[] {
        return [ClassRoomCreateModel.fromObject(this.form.value)];
    }

    protected override getForm(formBuilder: FormBuilder): FormGroup {
        return formBuilder.group({
            id: [null, Validators.required], tutor: [null, Validators.required], students: [null], courses: [null],
        });
    }

    private getUsers(accountType: AccountType): readonly UserModel[] {
        return this._users.filter((user: UserModel): boolean => user.accountType === accountType && !user.classroom);
    }
}
