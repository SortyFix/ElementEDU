import {Component, Inject} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MAT_DIALOG_DATA, MatDialogClose, MatDialogRef} from "@angular/material/dialog";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {SubjectModel} from "../subject-model";
import {CourseModel} from "../../course-model";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {NgForOf, NgIf} from "@angular/common";
import {SubjectService} from "../subject.service";

@Component({
  selector: 'app-delete-subjects',
    imports: [
        MatIcon,
        MatDialogClose,
        MatIconButton,
        MatCardTitle,
        MatCardHeader,
        MatCard,
        MatCardActions,
        MatButton,
        MatCardContent,
        MatChip,
        NgForOf,
        NgIf,
        MatChipSet,
    ],
  templateUrl: './delete-subjects.component.html',
  styleUrl: './delete-subjects.component.scss'
})
export class DeleteSubjectsComponent {

    private _affectedCourses!: readonly CourseModel[];
    private _finishedLoading: boolean = false;

    public constructor(
        @Inject(MAT_DIALOG_DATA) private readonly _data: { entries: readonly SubjectModel[] },
        private readonly ref: MatDialogRef<DeleteSubjectsComponent>,
        subjectService: SubjectService
    ) {
        subjectService.fetchCoursesLazily(_data.entries.map(
            (subject: SubjectModel): string => subject.id)
        ).subscribe((course: readonly CourseModel[]): void => {
            this._affectedCourses = course;
            this._finishedLoading = true;
        })
    }

    protected get finishedLoading(): boolean {
        return this._finishedLoading;
    }

    protected get affectedCourses(): string[] {
        return this._affectedCourses.map((course: CourseModel): string => course.name);
    }

    protected get entries(): string[] {
        return this._data.entries.map((model: SubjectModel): string => model.id);
    }

    protected onCancel(): void {
        this.ref.close(false);
    }

    protected onSubmit(): void {
        this.ref.close(true);
    }
}
