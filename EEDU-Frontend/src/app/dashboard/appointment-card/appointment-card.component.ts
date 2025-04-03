import {Component, OnInit} from '@angular/core';
import {AppointmentService} from "../../user/courses/appointment/appointment.service";
import {AppointmentEntryModel} from "../../user/courses/appointment/entry/appointment-entry-model";
import {NgForOf, NgIf} from "@angular/common";
import {CourseService} from "../../user/courses/course.service";

@Component({
    selector: 'app-appointment-card',
    standalone: true,
    imports: [NgForOf, NgIf],
    templateUrl: './appointment-card.component.html',
    styleUrl: './appointment-card.component.scss'
})
export class AppointmentCardComponent implements OnInit {

    constructor(appointmentService: AppointmentService, public courseService: CourseService) {
        appointmentService.nextAppointments.subscribe((appointments: readonly AppointmentEntryModel[]): void => {
            this._appointments = appointments;
        });
    }

    ngOnInit() {
        this.courseService.ownCourses$;
    }

    private _appointments: readonly AppointmentEntryModel[] = [];

    protected get appointments(): readonly AppointmentEntryModel[] {
        return this._appointments;
    }

    protected getDateString(model: AppointmentEntryModel)
    {
        return model.start.toLocaleDateString("en-GB");
    }
}
