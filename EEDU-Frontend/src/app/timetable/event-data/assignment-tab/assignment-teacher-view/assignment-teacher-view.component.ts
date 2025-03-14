import {Component, Input, input, InputSignal} from '@angular/core';
import {NgIf} from "@angular/common";
import {AppointmentEntryModel} from "../../../../user/courses/appointment/entry/appointment-entry-model";
import {SelectionInput} from "../../../../common/selection-input/selection-input.component";
import {MatList, MatListItem} from "@angular/material/list";
import {AssignmentInsightModel} from "../../../../user/courses/appointment/entry/assignment/assignment-insight-model";
import {AssignmentService} from "../../../../user/courses/appointment/entry/assignment/assignment.service";
import {AssignmentModel} from "../../../../user/courses/appointment/entry/assignment/assignment-model";

@Component({
    selector: 'app-assignment-teacher-view',
    standalone: true,
    imports: [
        NgIf,
        SelectionInput,
        MatList,
        MatListItem
    ],
    templateUrl: './assignment-teacher-view.component.html',
    styleUrl: './assignment-teacher-view.component.scss'
})
export class AssignmentTeacherViewComponent {

    private _appointment: AppointmentEntryModel | null = null;
    private _assignmentInsightModels: readonly AssignmentInsightModel[] = [];
    public readonly editing: InputSignal<boolean> = input<boolean>(false);

    private _currentInsight: AssignmentInsightModel | null = null;

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

    protected valueUpdate(event: readonly AssignmentInsightModel[] | AssignmentInsightModel): void {
        this._currentInsight = event as AssignmentInsightModel
    }

    protected get currentInsight(): AssignmentInsightModel | null {
        return this._currentInsight;
    }
}
