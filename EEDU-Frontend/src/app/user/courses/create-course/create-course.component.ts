import { Component } from '@angular/core';
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {SubjectModel} from "../subject/subject-model";
import {SubjectService} from "../subject/subject.service";
import {GeneralCreateComponent} from "../../../timetable/general-create-component/general-create.component";
import {CourseModel} from "../course-model";
import {CourseService} from "../course.service";
import {DialogRef} from "@angular/cdk/dialog";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {SelectionInput} from "../../../common/selection-input/selection-input.component";
import {AbstractCourseComponentsCreate} from "../abstract-course-components/create/abstract-course-components-create";
import {ReducedUserModel} from "../../reduced-user-model";
import {ClassRoomModel} from "../classroom/class-room-model";
import {ClassRoomService} from "../classroom/class-room.service";
import {UserService} from "../../user.service";
import {AccountType} from "../../account-type";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-create-course',
  standalone: true,
    imports: [
        MatCardContent,
        MatDialogClose,
        MatLabel,
        MatFormField,
        MatInput,
        GeneralCreateComponent,
        MatCardActions,
        MatButton,
        ReactiveFormsModule,
        SelectionInput,
    ],
  templateUrl: './create-course.component.html',
})
export class CreateCourseComponent extends AbstractCourseComponentsCreate<CourseModel> {

    private readonly _subjects: SubjectModel[] = [];
    private readonly _users: ReducedUserModel[] = [];
    private readonly _classrooms: ClassRoomModel[] = [];


    public constructor(courseService: CourseService, dialogRef: DialogRef, formBuilder: FormBuilder,
        private readonly _subjectService: SubjectService,
        private readonly _userService: UserService,
        private readonly _classroomService: ClassRoomService
    )
    {
        super(courseService, dialogRef, formBuilder, "Create Course");

        this._userService.fetchAllReduced.subscribe((users: ReducedUserModel[]): void =>
        {
            this._users.length = 0;
            this._users.push(...users);
        });


        this.subjects = this._subjectService.value;
        this._subjectService.value$.subscribe((subjects: SubjectModel[]): void => { this.subjects = subjects; });

        this.classrooms = this._classroomService.value;
        this._classroomService.value$.subscribe((classrooms: ClassRoomModel[]): void => { this.classrooms = classrooms; });
    }

    private set subjects(subjects: SubjectModel[])
    {
        this._subjects.length = 0;
        this._subjects.push(...subjects);
    }

    private set classrooms(classrooms: ClassRoomModel[])
    {
        this._classrooms.length = 0;
        this._classrooms.push(...classrooms);
    }

    protected override getForm(formBuilder: FormBuilder): FormGroup {
        return formBuilder.group({
            name: ['', Validators.required],
            subject: [null, Validators.required],
            teacher: [null, Validators.required],
            students: [null],
            classroom: [null],
        });
    }

    protected get teacher(): ReducedUserModel[]
    {
        return this.getUsers(AccountType.TEACHER);
    }

    protected get students(): ReducedUserModel[]
    {
        return this.getUsers(AccountType.STUDENT);
    }

    private getUsers(accountType: AccountType): ReducedUserModel[] {
        return this._users.filter((user: ReducedUserModel): boolean => user.accountType === accountType);
    }

    protected get classrooms(): ClassRoomModel[] {
        return this._classrooms;
    }

    protected override get loading(): boolean {
        return super.loading || !this._subjectService.fetched || !this._classroomService.fetched;
    }

    protected get subjects(): SubjectModel[] {
        return this._subjects;
    }
}
