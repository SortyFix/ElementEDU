import {Component, Inject} from '@angular/core';
import {AbstractList} from "../../../../common/abstract-list/abstract-list.component";
import {SubjectModel} from "../subject-model";
import {SubjectService} from "../subject.service";
import {MatIcon} from "@angular/material/icon";
import {MAT_DIALOG_DATA, MatDialog, MatDialogClose, MatDialogRef} from "@angular/material/dialog";
import {MatButton, MatIconButton} from "@angular/material/button";
import {NgIf} from "@angular/common";
import {MatProgressBar} from "@angular/material/progress-bar";
import {AbstractCourseComponentList} from "../../abstract-course-components/list/abstract-course-component-list";
import {DeleteDialogComponent} from "../../../../common/delete-dialog/delete-dialog.component";
import {MatChip, MatChipGrid, MatChipInput, MatChipRemove, MatChipRow, MatChipSet} from "@angular/material/chips";
import {AbstractDeleteDialog} from "../../abstract-course-components/delete/abstract-delete-dialog";
import {CourseModel} from "../../course-model";
import {FormBuilder, ReactiveFormsModule} from "@angular/forms";
import {
    AbstractCourseComponentsCreateMultiple
} from "../../abstract-course-components/create/abstract-course-components-create-multiple";
import {DialogRef} from "@angular/cdk/dialog";
import {GeneralCardComponent} from "../../../../common/general-card-component/general-card.component";
import {MatInput} from "@angular/material/input";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatCardActions, MatCardContent} from "@angular/material/card";

@Component({
    imports: [MatProgressBar, AbstractList, MatIconButton, MatButton, MatIcon, NgIf,],
    templateUrl: '../../abstract-course-components/list/abstract-course-components-list.html',
    styleUrl: '../../abstract-course-components/list/abstract-course-components-list.scss'
})
export class SubjectListComponent extends AbstractCourseComponentList<string, SubjectModel> {

    public constructor(service: SubjectService, dialog: MatDialog) {
        super(service, dialog, CreateSubjectComponent, DeleteSubjectComponent, {title: (value: SubjectModel): string => value.id});
    }
}

@Component({
    imports: [MatCardContent, MatCardActions, MatButton, MatDialogClose, MatLabel, MatFormField, MatInput, ReactiveFormsModule, GeneralCardComponent, MatIcon, MatChipRow, MatChipGrid, MatChipInput, MatChipRemove],
    templateUrl: '../../abstract-course-components/create/abstract-course-components-create-multiple.html',
})
export class CreateSubjectComponent extends AbstractCourseComponentsCreateMultiple<SubjectModel> {
    public constructor(subjectService: SubjectService, dialogRef: DialogRef, formBuilder: FormBuilder) {
        super(subjectService, dialogRef, formBuilder, "Create Subjects");
    }
}

@Component({
    imports: [DeleteDialogComponent, NgIf, MatChipSet, MatChip], templateUrl: './delete-subject.component.html'
})
export class DeleteSubjectComponent extends AbstractDeleteDialog<SubjectModel> {

    public constructor(@Inject(MAT_DIALOG_DATA) data: {
        entries: SubjectModel[]
    }, ref: MatDialogRef<DeleteSubjectComponent>, subjectService: SubjectService) {
        super(data, ref);
        subjectService.fetchCoursesLazily(this.entries).subscribe({
            next: (data: readonly CourseModel[]): void => {
                this._affectedCourses = data.map((course: CourseModel): string => course.name);
                this._loading = false;
            }
        })
    }

    private _affectedCourses: readonly string[] = [];

    protected get affectedCourses(): readonly string[] {
        return this._affectedCourses;
    }

    private _loading: boolean = true;

    protected get loading(): boolean {
        return this._loading;
    }

    protected override get entries(): string[] {
        return this.data.entries.map((subject: SubjectModel): string => subject.id);
    }
}
