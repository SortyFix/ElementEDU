import { Component } from '@angular/core';
import {AssignmentModel} from "../../user/courses/appointment/entry/assignment-model";
import {AppointmentService} from "../../user/courses/appointment/appointment.service";

@Component({
  selector: 'app-assignment-card',
  standalone: true,
  imports: [],
  templateUrl: './assignment-card.component.html',
  styleUrl: './assignment-card.component.scss'
})
export class AssignmentCardComponent {

    public constructor(private readonly _appointmentService: AppointmentService) {}

    protected get assignments(): readonly AssignmentModel[] {
        return this._appointmentService.nextAssignments.slice(0, 5);
    }
}
