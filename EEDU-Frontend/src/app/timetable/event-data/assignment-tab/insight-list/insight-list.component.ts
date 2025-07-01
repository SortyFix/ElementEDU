import {Component, input, InputSignal} from '@angular/core';
import {AssignmentInsightModel} from "../../../../user/courses/appointment/entry/assignment/assignment-insight-model";
import {NgIf} from "@angular/common";
import {AssignmentService} from "../../../../user/courses/appointment/entry/assignment/assignment.service";
import {AppointmentEntryModel} from "../../../../user/courses/appointment/entry/appointment-entry-model";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-insight-list',
    imports: [
        NgIf,
        MatIcon
    ],
  templateUrl: './insight-list.component.html',
  styleUrl: './insight-list.component.scss'
})
export class InsightListComponent {

    public readonly insight: InputSignal<AssignmentInsightModel | null> = input<AssignmentInsightModel | null>(null);
    public readonly appointment: InputSignal<AppointmentEntryModel | null> = input<AppointmentEntryModel | null>(null);

    public constructor(private readonly _assignmentService: AssignmentService) {}

    protected downloadFile(fileName: string): void
    {
           this._assignmentService.downloadAssignment(this.appointment()!.id, this.insight()!.user.id, fileName)
    }
}
