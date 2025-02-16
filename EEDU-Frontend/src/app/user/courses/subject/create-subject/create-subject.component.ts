import {Component} from '@angular/core';
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormBuilder, ReactiveFormsModule} from "@angular/forms";
import {DialogRef} from "@angular/cdk/dialog";
import {SubjectModel} from "../subject-model";
import {SubjectService} from "../subject.service";
import {GeneralCreateComponent} from "../../../../timetable/general-create-component/general-create.component";
import {AbstractCourseComponentsCreate} from "../../abstract-course-components-create";

@Component({
  selector: 'app-create-subject',
  standalone: true,
    imports: [
        MatCardContent,
        MatCardActions,
        MatButton,
        MatDialogClose,
        MatLabel,
        MatFormField,
        MatInput,
        ReactiveFormsModule,
        GeneralCreateComponent
    ],
  templateUrl: './create-subject.component.html',
  styleUrl: './create-subject.component.scss'
})
export class CreateSubjectComponent extends AbstractCourseComponentsCreate<SubjectModel> {
    public constructor(subjectService: SubjectService, dialogRef: DialogRef, formBuilder: FormBuilder) {
        super(subjectService, dialogRef, formBuilder);
    }
}
