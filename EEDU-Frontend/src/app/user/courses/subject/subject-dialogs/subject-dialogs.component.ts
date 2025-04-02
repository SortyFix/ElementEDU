import {Component, Inject} from '@angular/core';
import {SubjectModel} from "../subject-model";
import {SubjectService} from "../subject.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NgIf} from "@angular/common";
import {DeleteDialogComponent} from "../../../../common/delete-dialog/delete-dialog.component";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {AbstractDeleteDialog} from "../../abstract-course-components/delete/abstract-delete-dialog";
import {CourseModel} from "../../course-model";

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
