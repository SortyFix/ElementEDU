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
import {MatChipGrid, MatChipInput, MatChipRemove, MatChipRow} from "@angular/material/chips";
import {MatIcon} from "@angular/material/icon";
import {
    AbstractCourseComponentsCreateMultiple
} from "../../abstract-course-components/create/abstract-course-components-create-multiple";

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
        GeneralCreateComponent,
        MatIcon,
        MatChipRow,
        MatChipGrid,
        MatChipInput,
        MatChipRemove
    ],
    templateUrl: '../../abstract-course-components/create/abstract-course-components-create-multiple.html',
})
export class CreateSubjectComponent extends AbstractCourseComponentsCreateMultiple<SubjectModel> {
    public constructor(subjectService: SubjectService, dialogRef: DialogRef, formBuilder: FormBuilder) {
        super(subjectService, dialogRef, formBuilder, "Create Subjects");
    }
}
