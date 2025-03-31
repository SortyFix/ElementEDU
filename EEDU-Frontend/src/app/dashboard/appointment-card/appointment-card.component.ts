import {Component} from '@angular/core';
import {AppointmentService} from "../../user/courses/appointment/appointment.service";
import {AppointmentEntryModel} from "../../user/courses/appointment/entry/appointment-entry-model";
import {NgForOf, NgIf} from "@angular/common";

@Component({
    selector: 'app-appointment-card',
    standalone: true,
    imports: [NgForOf, NgIf],
    templateUrl: './appointment-card.component.html',
    styleUrl: './appointment-card.component.scss'
})
export class AppointmentCardComponent {

    public constructor(appointmentService: AppointmentService) {
        appointmentService.nextAppointments.subscribe((appointments: readonly AppointmentEntryModel[]): void => {
            this._appointments = appointments;
        });
    }

    private _appointments: readonly AppointmentEntryModel[] = [];

    protected get appointments(): readonly AppointmentEntryModel[] {
        return this._appointments;
    }
}
