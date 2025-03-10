import {Component} from '@angular/core';
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {SelectionInput} from "../../../common/selection-input/selection-input.component";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatFormField, MatHint, MatLabel} from "@angular/material/form-field";
import {GeneralCardComponent} from "../../../common/general-card-component/general-card.component";
import {AbstractCreateEntity} from "../../../entity/create-entity/abstract-create-entity";
import {ReducedUserModel} from "../../reduced-user-model";
import {CourseService} from "../course.service";
import {DialogRef} from "@angular/cdk/dialog";
import {SubjectService} from "../subject/subject.service";
import {UserService} from "../../user.service";
import {ClassRoomService} from "../classroom/class-room.service";
import {SubjectModel} from "../subject/subject-model";
import {ClassRoomModel} from "../classroom/class-room-model";
import {CourseCreateModel} from "../course-create-model";
import {AccountType} from "../../account-type";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatInput} from "@angular/material/input";
import {NgIf} from "@angular/common";
import {GeneralErrorBoxComponent} from "../../../common/general-error-box/general-error-box.component";

@Component({
    imports: [MatCardActions, SelectionInput, ReactiveFormsModule, MatHint, MatLabel, MatFormField, MatCardContent, GeneralCardComponent, MatButton, MatDialogClose, MatInput, NgIf, GeneralErrorBoxComponent],
    templateUrl: './create-course-dialog.component.html'
})
export class CreateCourseDialogComponent extends AbstractCreateEntity {

    private _users: readonly ReducedUserModel[] = [];

    public constructor(courseService: CourseService, dialogRef: DialogRef, formBuilder: FormBuilder, private readonly _subjectService: SubjectService, private readonly _userService: UserService, private readonly _classroomService: ClassRoomService,) {
        super(courseService, dialogRef, formBuilder, "Create Course");

        this._userService.fetchAllReduced.subscribe((users: ReducedUserModel[]): void => {
            this._users = users;
            const teacher: readonly ReducedUserModel[] = this.teacher;
            if(teacher.length === 1)
            {
                this.form.get('teacher')?.setValue(teacher[0]);
            }
        });

        this.subjects = this._subjectService.value;
        this._subjectService.value$.subscribe((subjects: SubjectModel[]): void => { this.subjects = subjects; });

        this.classrooms = this._classroomService.value;
        this._classroomService.value$.subscribe((classrooms: ClassRoomModel[]): void => { this.classrooms = classrooms; });
    }

    private _subjects: readonly SubjectModel[] = [];

    protected get subjects(): readonly SubjectModel[] {
        return this._subjects;
    }

    private set subjects(subjects: SubjectModel[]) {
        this._subjects = subjects;
    }

    private _classrooms: readonly ClassRoomModel[] = [];

    protected get classrooms(): readonly ClassRoomModel[] {
        return this._classrooms;
    }

    private set classrooms(classrooms: ClassRoomModel[]) {
        this._classrooms = classrooms;
    }

    protected get eligibleTeacher(): boolean {
        return this.loading || this.teacher.length > 0;
    }

    protected override get createModel(): any[] {
        return [CourseCreateModel.fromObject(this.form.value)];
    }

    protected get teacher(): readonly ReducedUserModel[] {
        return this.getUsers(AccountType.TEACHER);
    }

    protected get students(): readonly  ReducedUserModel[] {
        return this.getUsers(AccountType.STUDENT);
    }

    protected override get loading(): boolean {
        return !this._subjectService.fetched || !this._classroomService.fetched;
    }

    protected override getForm(formBuilder: FormBuilder): FormGroup {
        return formBuilder.group({
            name: [null, Validators.required],
            subject: [null, Validators.required],
            teacher: [null, Validators.required],
            students: [null],
            classroom: [null],
        });
    }

    private getUsers(accountType: AccountType): readonly ReducedUserModel[] {
        return this._users.filter((user: ReducedUserModel): boolean => user.accountType === accountType);
    }
}
