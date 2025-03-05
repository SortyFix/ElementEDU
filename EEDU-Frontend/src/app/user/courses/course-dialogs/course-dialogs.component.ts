import {Component, Inject, Type} from '@angular/core';
import {AbstractList} from "../../../common/abstract-list/abstract-list.component";
import {CourseModel} from "../course-model";
import {CourseService} from "../course.service";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MAT_DIALOG_DATA, MatDialog, MatDialogClose, MatDialogRef} from "@angular/material/dialog";
import {NgIf} from "@angular/common";
import {MatProgressBar} from "@angular/material/progress-bar";
import {AbstractCourseComponentList} from "../abstract-course-components/list/abstract-course-component-list";
import {ListItemContent} from "../../../common/abstract-list/list-item-content";
import {CourseListItemComponent} from "./course-list-item/course-list-item.component";
import {DeleteDialogComponent} from "../../../common/delete-dialog/delete-dialog.component";
import {AbstractDeleteDialog} from "../abstract-course-components/delete/abstract-delete-dialog";
import {AccountType} from '../../account-type';
import {ReducedUserModel} from '../../reduced-user-model';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {SubjectModel} from "../subject/subject-model";
import {ClassRoomModel} from "../classroom/class-room-model";
import {CourseCreateModel} from "../course-create-model";
import {ClassRoomService} from "../classroom/class-room.service";
import {UserService} from "../../user.service";
import {SubjectService} from "../subject/subject.service";
import {DialogRef} from "@angular/cdk/dialog";
import {AbstractCourseComponentsCreate} from "../abstract-course-components/create/abstract-course-components-create";
import {SelectionInput} from "../../../common/selection-input/selection-input.component";
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {GeneralCardComponent} from "../../../common/general-card-component/general-card.component";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";

@Component({
    imports: [MatProgressBar, AbstractList, MatIconButton, MatButton, MatIcon, NgIf],
    templateUrl: '../abstract-course-components/list/abstract-course-components-list.html',
    styleUrl: '../abstract-course-components/list/abstract-course-components-list.scss'
})
export class CourseListComponent extends AbstractCourseComponentList<bigint, CourseModel> {

    public constructor(service: CourseService, dialog: MatDialog) {
        super(service, dialog, CreateCourseComponent, DeleteCourseComponent, {
            title: (value: CourseModel): string => value.name,
            chips: (value: CourseModel): string[] => [
                `${value.teacher?.name}`,
                `${value.students?.length} Student(s)`,
                value.subject.id,
                `${value.appointmentEntries.length} Appointment(s)`,
                `${value.frequentAppointments.length} Frequent Appointment(s)`,
            ]
        });
    }

    protected override get content(): Type<ListItemContent<CourseModel>> | null {
        return CourseListItemComponent;
    }
}

@Component({
    imports: [MatCardContent, MatDialogClose, MatLabel, MatFormField, MatInput, GeneralCardComponent, MatCardActions, MatButton, ReactiveFormsModule, SelectionInput],
    templateUrl: './create-course.component.html',
})
export class CreateCourseComponent extends AbstractCourseComponentsCreate<CourseModel> {

    private _users: readonly ReducedUserModel[] = [];

    public constructor(courseService: CourseService, dialogRef: DialogRef, formBuilder: FormBuilder, private readonly _subjectService: SubjectService, private readonly _userService: UserService, private readonly _classroomService: ClassRoomService,) {
        super(courseService, dialogRef, formBuilder, "Create Course");

        this._userService.fetchAllReduced.subscribe((users: ReducedUserModel[]): void => {
            this._users = users;
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
        return super.loading || !this._subjectService.fetched || !this._classroomService.fetched;
    }

    protected override get canSubmit(): boolean {
        return true;
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

@Component({
    imports: [DeleteDialogComponent],
    template: '<app-delete-dialog title="course(s)" [entries]="entries" [ref]="ref"></app-delete-dialog>'
})
export class DeleteCourseComponent extends AbstractDeleteDialog<CourseModel> {

    public constructor(@Inject(MAT_DIALOG_DATA) data: {
        entries: CourseModel[]
    }, ref: MatDialogRef<DeleteCourseComponent>) { super(data, ref); }

    protected override get entries(): string[] {
        return this.data.entries.map((course: CourseModel): string => course.name);
    }
}

