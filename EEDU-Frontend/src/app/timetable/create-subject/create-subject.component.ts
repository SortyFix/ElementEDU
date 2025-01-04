import { Component } from '@angular/core';
import {GeneralCreateComponent} from "../general-create-component/general-create.component";
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {SubjectService} from "../../user/courses/subject/subject.service";
import {DialogRef} from "@angular/cdk/dialog";
import {SubjectModel} from "../../user/courses/subject/subject-model";

@Component({
  selector: 'app-create-subject',
  standalone: true,
    imports: [
        GeneralCreateComponent,
        MatCardContent,
        MatCardActions,
        MatButton,
        MatDialogClose,
        MatLabel,
        MatFormField,
        MatInput,
        ReactiveFormsModule
    ],
  templateUrl: './create-subject.component.html',
  styleUrl: './create-subject.component.scss'
})
export class CreateSubjectComponent {

    private readonly _form: FormGroup;

    private _loading: boolean = true;

    public constructor(private _subjectService: SubjectService, private _dialogRef: DialogRef, formBuilder: FormBuilder) {
        this._form = formBuilder.group({ name: [null, Validators.required] });
        this._subjectService.fetchSubjects().subscribe((subjects: SubjectModel[]): void => { this.loading = false; })
    }

    protected get loading(): boolean {
        return this._loading;
    }

    private set loading(value: boolean) {
        this._loading = value;
    }

    private get dialogRef(): DialogRef {
        return this._dialogRef;
    }

    private get subjectService(): SubjectService {
        return this._subjectService;
    }

    protected get form(): FormGroup {
        return this._form;
    }

    protected onSubmit(): void
    {
        if(this.form.invalid)
        {
            return;
        }

        console.log()

        this.subjectService.createSubject([this.form.value]).subscribe({
            next: (result: SubjectModel[]): void => { this.dialogRef.close(); },
        })
    }

    protected get canSubmit(): boolean
    {
        return this.form.valid;
    }
}
