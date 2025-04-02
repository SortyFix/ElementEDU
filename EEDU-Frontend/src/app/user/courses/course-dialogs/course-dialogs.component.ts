import {Component, Inject} from '@angular/core';
import {CourseModel} from "../course-model";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {DeleteDialogComponent} from "../../../common/delete-dialog/delete-dialog.component";
import {AbstractDeleteDialog} from "../abstract-course-components/delete/abstract-delete-dialog";

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

