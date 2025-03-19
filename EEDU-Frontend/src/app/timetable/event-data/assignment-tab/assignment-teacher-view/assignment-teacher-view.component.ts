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
import {AssessmentModel} from "../../../../user/courses/appointment/entry/assignment/assessment/assessment-model";
import {
    AssessmentCreateModel
} from "../../../../user/courses/appointment/entry/assignment/assessment/assessment-create-model";
import {GeneralErrorBoxComponent} from "../../../../common/general-error-box/general-error-box.component";

@Component({
    selector: 'app-assignment-teacher-view',
    standalone: true,
    imports: [NgIf, MatLabel, MatFormField, MatSelect, MatOption, MatIcon, MatButton, MatInput, ReactiveFormsModule, GeneralErrorBoxComponent],
    templateUrl: './assignment-teacher-view.component.html',
    styleUrl: './assignment-teacher-view.component.scss'
})
export class AssignmentTeacherViewComponent {

    private _appointment: AppointmentEntryModel | null = null;
    private _assignmentInsight: readonly AssignmentInsightModel[] = [];
    private _insight: AssignmentInsightModel | null = null;
    public readonly editing: InputSignal<boolean> = input<boolean>(false);

    private readonly _assessForm: FormGroup;

    public constructor(
        private readonly _assignmentService: AssignmentService,
        private readonly _assessmentService: AssessmentService,
        private readonly _userService: UserService,
        formBuilder: FormBuilder) {
        this._assessForm = formBuilder.group({
            feedback: [null]
        })
    }

    protected get assessForm(): FormGroup {
        return this._assessForm;
    }

    protected set currentInsight(assignmentInsight: AssignmentInsightModel)
    {
        this._insight = assignmentInsight;
    }

    protected get currentInsight(): AssignmentInsightModel | null
    {
        return this._insight;
    }

    protected onAssess(): void
    {
        if(!this.currentInsight)
        {
            return;
        }

        const insight: AssignmentInsightModel = this.currentInsight;
        this._assessmentService.assess([AssessmentCreateModel.fromObject({
            appointment: Number(this.appointment?.id),
            user: insight.user.id,
            feedback: this.assessForm.get('feedback')?.value,
        })]).subscribe((assessmentModel: readonly AssessmentModel[]): void => {
            this._assignmentInsight.map((current: AssignmentInsightModel): AssignmentInsightModel => {

                if(current === insight)
                {
                    const newInsight: AssignmentInsightModel = AssignmentInsightModel.pushAssessment(current, assessmentModel[0]);
                    if(this.currentInsight === insight)
                    {
                        this.currentInsight = newInsight;
                    }
                    return newInsight;
                }

                return current;
            })
        });
    }

    @Input() public set appointment(appointment: AppointmentEntryModel) {
        this._appointment = appointment;
        this._assignmentService.fetchInsights(appointment.id).subscribe((response: AssignmentInsightModel[]): void =>
        {
            this._assignmentInsight = response;
        })
    }

    public get appointment(): AppointmentEntryModel | null {
        return this._appointment;
    }

    protected get assignmentInsight(): readonly AssignmentInsightModel[] {
        return this._assignmentInsight;
    }

    protected get assignment(): AssignmentModel | null {
        return this.appointment!.assignment || null;
    }

    protected toIcon(insight: AssignmentInsightModel): 'assignment_turned_in' | 'assignment_late' | 'pending' {
        if (this.assignment?.submitUntil.getTime() && (this.assignment?.submitUntil.getTime()) < new Date().getTime()) {
            return insight.submitted ? 'assignment_turned_in' : 'assignment_late';
        }
        return insight.submitted ? 'assignment_turned_in' : 'pending';
    }
}
