import { Component } from '@angular/core';
import {AppointmentService} from "../../user/courses/appointment/appointment.service";
import {AppointmentEntryModel} from "../../user/courses/appointment/entry/appointment-entry-model";

@Component({
  selector: 'app-appointment-card',
  standalone: true,
  imports: [],
  templateUrl: './appointment-card.component.html',
  styleUrl: './appointment-card.component.scss'
})
export class AppointmentCardComponent {

    public constructor(private readonly _appointmentService: AppointmentService) {}

    protected get appointments(): readonly AppointmentEntryModel[] {
        return this._appointmentService.nextAppointments.slice(0, 5);
    }
}
