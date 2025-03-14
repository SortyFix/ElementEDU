import {AfterViewInit, Component, Input} from '@angular/core';
import {AppointmentEntryModel} from "../../../../user/courses/appointment/entry/appointment-entry-model";
import {FileUploadComponent} from "../../../../common/file-upload/file-upload.component";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatList, MatListItem, MatListItemLine, MatListItemTitle} from "@angular/material/list";
import {NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {AssignmentService} from "../../../../user/courses/appointment/entry/assignment/assignment.service";
import {AssignmentInsightModel} from "../../../../user/courses/appointment/entry/assignment/assignment-insight-model";
import {AssignmentModel} from "../../../../user/courses/appointment/entry/assignment/assignment-model";

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
export class AssignmentStudentViewComponent implements AfterViewInit {

    @Input() public appointment!: AppointmentEntryModel;
    private readonly _form: FormGroup;
    private _canSubmit: boolean = false;
    private _insight?: AssignmentInsightModel;


    protected get insight(): AssignmentInsightModel | undefined {
        return this._insight;
    }

    public constructor(private readonly _assignmentService: AssignmentService, form: FormBuilder) {
        this._form = form.group({files: [[], Validators.required]});
    }

    public ngAfterViewInit(): void {
        this._assignmentService.fetchInsight(this.appointment.id).subscribe((insights: AssignmentInsightModel): void => {
            this._insight = insights;
        });
    }

    protected get form(): FormGroup {
        return this._form;
    }

    protected get assignment(): AssignmentModel | null {
        return this.appointment.assignment || null;
    }

    protected onSubmit(): void {
        // this will be part of my flashback, when I reveal my story why I became evil and corrupted
        const fileArray: File[] = [];
        const files: FileList = this.form.get("files")?.value as FileList;
        for (let i: number = 0; i < files.length; i++) {
            fileArray.push(files.item(i) as File);
        }
        // --

        this._assignmentService.submitAssignment(this.appointment.id, fileArray).subscribe((): void => {});
    }

    protected onFileSelected(event: FileList): void {
        this._canSubmit = event.length > 0
    }

    protected get canSubmit(): boolean {
        return this._canSubmit;
    }
}
