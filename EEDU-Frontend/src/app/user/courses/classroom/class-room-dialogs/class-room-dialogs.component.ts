import {Component, Inject} from '@angular/core';
import {ClassRoomModel} from "../class-room-model";
import {ClassRoomService} from "../class-room.service";
import {MAT_DIALOG_DATA, MatDialogClose, MatDialogRef} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import {DeleteDialogComponent} from "../../../../common/delete-dialog/delete-dialog.component";
import {AbstractDeleteDialog} from "../../abstract-course-components/delete/abstract-delete-dialog";
import {CourseModel} from "../../course-model";
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatInput} from "@angular/material/input";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {GeneralCardComponent} from "../../../../common/general-card-component/general-card.component";
import {SelectionInput} from "../../../../common/selection-input/selection-input.component";
import {
    AbstractCourseComponentsCreate
} from "../../abstract-course-components/create/abstract-course-components-create";
import {DialogRef} from "@angular/cdk/dialog";
import {UserService} from "../../../user.service";
import {CourseService} from "../../course.service";
import {UserModel} from "../../../user-model";
import {ClassRoomCreateModel} from "../class-room-create-model";
import {AccountType} from "../../../account-type";

@Component({
    imports: [MatCardActions, MatButton, MatDialogClose, ReactiveFormsModule, MatInput, MatLabel, MatFormField, MatCardContent, GeneralCardComponent, SelectionInput],
    templateUrl: './create-class-room.component.html',
})
export class CreateClassRoomComponent extends AbstractCourseComponentsCreate<ClassRoomModel> {

    private _users: readonly UserModel[] = [];
    private _courses: readonly CourseModel[] = [];

    public constructor(service: ClassRoomService, dialogRef: DialogRef, formBuilder: FormBuilder, userService: UserService, private readonly _courseService: CourseService) {
        super(service, dialogRef, formBuilder, "Create Class Room");

        userService.fetchAll.subscribe((user: UserModel[]): void => { this._users = user; });
        this._courseService.ownCourses$.subscribe((course: CourseModel[]): void => { this._courses = course; });
    }

    protected get courses(): readonly CourseModel[] { return this._courses; }

    protected override get loading(): boolean {
        return super.loading && this._courseService.fetched;
    }

    protected override get createModel(): ClassRoomCreateModel[] {
        return [ClassRoomCreateModel.fromObject(this.form.value)];
    }

    protected get teacher(): readonly UserModel[] {
        return this.getUsers(AccountType.TEACHER);
    }

    protected get students(): readonly UserModel[] {
        return this.getUsers(AccountType.STUDENT);
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

@Component({
    imports: [DeleteDialogComponent],
    template: '<app-delete-dialog title="classes(s)" [entries]="entries" [ref]="ref"></app-delete-dialog>'
})
export class DeleteClassRoomComponent extends AbstractDeleteDialog<ClassRoomModel> {

    public constructor(@Inject(MAT_DIALOG_DATA) data: {
        entries: ClassRoomModel[]
    }, ref: MatDialogRef<DeleteClassRoomComponent>) { super(data, ref); }

    protected override get entries(): string[] {
        return this.data.entries.map((classroom: ClassRoomModel): string => classroom.id);
    }
}

