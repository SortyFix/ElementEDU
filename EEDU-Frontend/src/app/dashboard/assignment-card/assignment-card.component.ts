import { Component } from '@angular/core';
import {AssignmentService} from "../../user/courses/appointment/entry/assignment/assignment.service";
import {AssignmentModel} from "../../user/courses/appointment/entry/assignment/assignment-model";

@Component({
  selector: 'app-assignment-card',
  standalone: true,
  imports: [],
  templateUrl: './assignment-card.component.html',
  styleUrl: './assignment-card.component.scss'
})
export class AssignmentCardComponent {

    private _assignments: readonly AssignmentModel[] = [];

    public constructor(assignmentService: AssignmentService)
    {
        assignmentService.nextAssignments.subscribe((assignment: readonly AssignmentModel[]): void =>
        {
            this._assignments = assignment;
        })
    }

    protected get assignments(): readonly AssignmentModel[] {
        return this._assignments.slice(0, 5);
    }
}
