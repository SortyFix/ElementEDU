import {Component, Input, input, InputSignal} from '@angular/core';
import {NgIf} from "@angular/common";
import {AppointmentEntryModel} from "../../../../user/courses/appointment/entry/appointment-entry-model";
import {MatList, MatListItem} from "@angular/material/list";
import {AssignmentInsightModel} from "../../../../user/courses/appointment/entry/assignment/assignment-insight-model";
import {AssignmentService} from "../../../../user/courses/appointment/entry/assignment/assignment.service";
import {AssignmentModel} from "../../../../user/courses/appointment/entry/assignment/assignment-model";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatOption, MatSelect} from "@angular/material/select";
import {MatIcon} from "@angular/material/icon";

@Component({
    selector: 'app-assignment-teacher-view',
    standalone: true,
    imports: [
        NgIf,
        MatLabel,
        MatList,
        MatFormField,
        MatSelect,
        MatOption,
        MatListItem,
        MatIcon
    ],
    templateUrl: './assignment-teacher-view.component.html',
    styleUrl: './assignment-teacher-view.component.scss'
})
export class AssignmentTeacherViewComponent {

    private _appointment: AppointmentEntryModel | null = null;
    private _assignmentInsightModels: readonly AssignmentInsightModel[] = [];
    public readonly editing: InputSignal<boolean> = input<boolean>(false);

    public constructor(private readonly _assignmentService: AssignmentService) {
    }

    @Input()
    public set appointment(appointment: AppointmentEntryModel)
    {
        this._appointment = appointment;
        this._assignmentService.fetchInsights(appointment.id).subscribe((response: AssignmentInsightModel[]): void =>
        {
            this._assignmentInsightModels = response;
        })
    }

    public get appointment(): AppointmentEntryModel | null {
        return this._appointment;
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

    protected toIcon(insight: AssignmentInsightModel): 'assignment_turned_in' | 'assignment_late' | 'pending'
    {
        if(this.assignment?.submitUntil.getTime() && (this.assignment?.submitUntil.getTime()) < new Date().getTime())
        {
            return insight.submitted ? 'assignment_turned_in' : 'assignment_late';
        }
        return insight.submitted ? 'assignment_turned_in' : 'pending';
    }
}
