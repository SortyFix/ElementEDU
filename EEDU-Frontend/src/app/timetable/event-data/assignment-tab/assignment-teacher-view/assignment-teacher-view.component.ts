import {Component, Input, input, InputSignal} from '@angular/core';
import {NgIf} from "@angular/common";
import {AppointmentEntryModel} from "../../../../user/courses/appointment/entry/appointment-entry-model";
import {AssignmentInsightModel} from "../../../../user/courses/appointment/entry/assignment/assignment-insight-model";
import {AssignmentService} from "../../../../user/courses/appointment/entry/assignment/assignment.service";
import {AssignmentModel} from "../../../../user/courses/appointment/entry/assignment/assignment-model";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatOption, MatSelect} from "@angular/material/select";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {FormBuilder, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {AssessmentService} from "../../../../user/courses/appointment/entry/assignment/assessment/assessment.service";
import {UserService} from "../../../../user/user.service";
import {MatSlider, MatSliderThumb} from "@angular/material/slider";
import {MatCheckbox} from "@angular/material/checkbox";

@Component({
    selector: 'app-assignment-teacher-view',
    standalone: true,
    imports: [NgIf, MatLabel, MatFormField, MatSelect, MatOption, MatIcon, MatButton, MatInput, ReactiveFormsModule, MatSlider, MatSliderThumb, MatCheckbox,],
    templateUrl: './assignment-teacher-view.component.html',
    styleUrl: './assignment-teacher-view.component.scss'
})
export class AssignmentTeacherViewComponent {

    private _appointment: AppointmentEntryModel | null = null;
    private _assignmentInsightModels: readonly AssignmentInsightModel[] = [];
    public readonly editing: InputSignal<boolean> = input<boolean>(false);

    private readonly _assessForm: FormGroup;

    public constructor(
        private readonly _assignmentService: AssignmentService,
        private readonly _assessmentService: AssessmentService,
        private readonly _userService: UserService,
        formBuilder: FormBuilder) {
        this._assessForm = formBuilder.group({
            grade: [null],
            feedback: [null]
        })
    }

    protected get assessForm(): FormGroup {
        return this._assessForm;
    }

    protected onAssess(): void
    {
        this._assessmentService.assess([{
            appointment: Number(this.appointment?.id),
            user: Number(this._userService.getUserData.id),
            feedback: this.assessForm.get('feedback')?.value,
            grade: this.assessForm.get('grade')?.value
        }]).subscribe();
    }

    public get appointment(): AppointmentEntryModel | null {
        return this._appointment;
    }

    @Input() public set appointment(appointment: AppointmentEntryModel) {
        this._appointment = appointment;
        this._assignmentService.fetchInsights(appointment.id).subscribe((response: AssignmentInsightModel[]): void =>
        {
            this._assignmentInsightModels = response;
        })
    }

    protected get assignmentInsightModels(): readonly AssignmentInsightModel[] {
        return this._assignmentInsightModels;
    }

    protected get assignment(): AssignmentModel | null {
        return this.appointment!.assignment || null;
    }

    protected toArray(value: any): readonly string[]
    {
        if(value instanceof AssignmentInsightModel)
        {
            return value.files;
        }
        return [];
    }

    protected toIcon(insight: AssignmentInsightModel): 'assignment_turned_in' | 'assignment_late' | 'pending' {
        if (this.assignment?.submitUntil.getTime() && (this.assignment?.submitUntil.getTime()) < new Date().getTime()) {
            return insight.submitted ? 'assignment_turned_in' : 'assignment_late';
        }
        return insight.submitted ? 'assignment_turned_in' : 'pending';
    }
}
