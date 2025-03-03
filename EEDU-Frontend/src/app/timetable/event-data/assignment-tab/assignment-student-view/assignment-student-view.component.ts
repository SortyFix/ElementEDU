import {Component, Input} from '@angular/core';
import {AppointmentEntryModel} from "../../../../user/courses/appointment/entry/appointment-entry-model";
import {FileUploadComponent} from "../../../../common/file-upload/file-upload.component";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatList, MatListItem, MatListItemLine, MatListItemTitle} from "@angular/material/list";
import {NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {AssignmentModel} from "../../../../user/courses/appointment/entry/assignment-model";

@Component({
    selector: 'app-assignment-student-view',
    imports: [
        FileUploadComponent,
        ReactiveFormsModule,
        MatButton,
        MatList,
        MatListItemLine,
        MatListItemTitle,
        MatListItem,
        NgIf,
        MatIcon
    ],
    templateUrl: './assignment-student-view.component.html',
    styleUrl: './assignment-student-view.component.scss'
})
export class AssignmentStudentViewComponent {

    @Input() public appointment!: AppointmentEntryModel;
    private readonly _form: FormGroup;

    public constructor(form: FormBuilder) {
        this._form = form.group({files: [[], Validators.required]});
    }

    protected get form(): FormGroup {
        return this._form;
    }

    protected get assignment(): AssignmentModel | null {
        return this.appointment.assignment || null;
    }

    protected onSubmit(): void {

    }
}
