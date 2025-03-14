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

    public constructor(private readonly _assignmentService: AssignmentService) {}

    protected get assignments(): readonly AssignmentModel[] {
        return this._assignmentService.nextAssignments.slice(0, 5);
    }
}
