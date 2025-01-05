import { Component } from '@angular/core';
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {SubjectModel} from "../subject/subject-model";
import {SubjectService} from "../subject/subject.service";
import {GeneralCreateComponent} from "../../../timetable/general-create-component/general-create.component";
import {GeneralSelectionInput} from "../../../timetable/general-selection-input/general-selection-input.component";

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
        GeneralSelectionInput
    ],
  templateUrl: './create-course.component.html',
  styleUrl: './create-course.component.scss'
})
export class CreateCourseComponent {

    private _loading: boolean = false;
    private readonly _subjects: SubjectModel[] = [];

    public constructor(private _subjectService: SubjectService) {
        this._subjectService.fetchSubjects().subscribe({
            next: (subjects: SubjectModel[]): void => {
                this._subjects.push(...subjects);
                this._subjectService.subjects$.subscribe((subjects: SubjectModel[]): void => {
                    this._subjects.length = 0;
                    this._subjects.push(...subjects);
                });
                this.loading = false;
            }
        });
    }

    protected get subjects(): SubjectModel[] {
        return this._subjects;
    }

    protected get loading(): boolean {
        return this._loading;
    }

    private set loading(loading: boolean)
    {
        this._loading = loading;
    }
}
