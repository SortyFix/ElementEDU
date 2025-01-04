import { Component } from '@angular/core';
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {GeneralSelectionInput} from "../create-appointment/general-selection-input/general-selection-input.component";
import {GeneralCreateComponent} from "../general-create-component/general-create.component";
import {SubjectModel} from "../../user/courses/subject/subject-model";
import {SubjectService} from "../../user/courses/subject/subject.service";

@Component({
  selector: 'app-create-course',
  standalone: true,
    imports: [
        MatCardContent,
        MatDialogClose,
        MatLabel,
        MatFormField,
        MatInput,
        GeneralSelectionInput,
        GeneralCreateComponent,
        MatCardActions,
        MatButton
    ],
  templateUrl: './create-course.component.html',
  styleUrl: './create-course.component.scss'
})
export class CreateCourseComponent {

    private _loading: boolean = false;
    private readonly _subjects: SubjectModel[] = [];


    public constructor(private _subjectService: SubjectService) {
        this._subjectService.fetchSubjects().subscribe();
        this._subjectService.subjects$.subscribe((subjects: SubjectModel[]): void => {
            this._subjects.length = 0;
            this._subjects.push(...subjects);
        });
    }


    protected get subjects(): SubjectModel[] {
        return this._subjects;
    }

    protected get loading(): boolean {
        return this._loading;
    }
}
