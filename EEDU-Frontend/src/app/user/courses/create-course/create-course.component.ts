import { Component } from '@angular/core';
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {SubjectModel} from "../subject/subject-model";
import {SubjectService} from "../subject/subject.service";
import {GeneralCreateComponent} from "../../../timetable/general-create-component/general-create.component";
import {AbstractCreateComponent} from "../abstract-create-component";
import {CourseModel} from "../course-model";
import {CourseService} from "../course.service";
import {DialogRef} from "@angular/cdk/dialog";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {SelectionInput} from "../../../common/selection-input/selection-input.component";

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
        SelectionInput
    ],
  templateUrl: './create-course.component.html',
  styleUrl: './create-course.component.scss'
})
export class CreateCourseComponent extends AbstractCreateComponent<CourseModel> {

    private readonly _subjects: SubjectModel[] = [];

    public constructor(courseService: CourseService, dialogRef: DialogRef, formBuilder: FormBuilder,
        private _subjectService: SubjectService)
    {
        super(courseService, dialogRef, formBuilder);

        this._subjectService.value$.subscribe((subjects: SubjectModel[]): void => {
            this._subjects.length = 0;
            this._subjects.push(...subjects);
        });
    }

    protected override getForm(formBuilder: FormBuilder): FormGroup {
        return formBuilder.group({
            name: ['', Validators.required],
            subject: [null, Validators.required],
            clazz: [null],
            users: [null]
        });
    }

    protected override get loading(): boolean {
        return super.loading || !this._subjectService.fetched;
    }

    protected get subjects(): SubjectModel[] {
        return this._subjects;
    }
}
