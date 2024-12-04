import {Component, OnInit, ViewChild} from '@angular/core';
import {CourseService} from "../user/courses/course.service";
import {CourseModel} from "../user/courses/models/course-model";
import {Calendar, CalendarOptions, EventMountArg, ViewApi} from "@fullcalendar/core";
import {ScheduledAppointmentModel} from "../user/courses/models/scheduled-appointment-model";
import {FullCalendarComponent, FullCalendarModule} from "@fullcalendar/angular";
import {UserService} from "../user/user.service";
import {RRule} from "rrule";
import {AccessibilityService} from "../accessibility.service";

import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridDay from '@fullcalendar/timegrid';
import rrulePlugin from '@fullcalendar/rrule'
import {MatCardTitle} from "@angular/material/card";

@Component({
  selector: 'app-timetable',
  standalone: true,
    imports: [
        FullCalendarModule,
        MatCardTitle
    ],
  templateUrl: './timetable.component.html',
  styleUrl: './timetable.component.scss'
})
export class TimetableComponent implements OnInit{


    @ViewChild('calendar') calendarComponent?: FullCalendarComponent;
    private readonly _calendarOptions: CalendarOptions = {
        plugins: [dayGridPlugin, timeGridDay, rrulePlugin],
        initialView: this.accessibilityService.mobile ? 'timeGridDay' : 'dayGridMonth',
        eventTimeFormat: {
            hour: '2-digit', minute: '2-digit', hour12: false
        },
        eventDidMount: (info: EventMountArg): void => {
            info.el.style.color = this.userService.getUserData.theme.getTextColor('widget', false);
        },
        windowResize: (arg: {view: ViewApi}): void => {
            arg.view.calendar.changeView(this.accessibilityService.mobile ? 'timeGridDay' : 'dayGridMonth')
        },
        events: [],
    };

    constructor(private userService: UserService, private accessibilityService: AccessibilityService, private courseService: CourseService) {}

    ngOnInit(): void {
        this.courseService.fetchCourses().subscribe((courses: CourseModel[]): void => {
            const api: Calendar = this.calendarComponent!.getApi();

            courses.forEach(({ name, appointments }: CourseModel): void =>
                appointments.forEach(({ start, duration, period }: ScheduledAppointmentModel): void => {
                    const timeStamp: number = Number(start) * 1000;
                    const startDate: string = new Date(timeStamp).toISOString();
                    const endDate: string = new Date(timeStamp + Number(duration) * 1000).toISOString();

                    api.addEvent({
                        title: name,
                        start: startDate,
                        end: endDate,
                        rrule: {
                            freq: RRule.MINUTELY,
                            interval: Number(period) / 60,
                            dtstart: startDate,
                            count: 52,
                        },
                    });
                })
            );
        });
    }

    protected get calendarOptions(): CalendarOptions {
        return this._calendarOptions;
    }
}
