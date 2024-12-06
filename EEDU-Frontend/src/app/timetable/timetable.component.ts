import {Component, OnInit, ViewChild} from '@angular/core';
import {CourseService} from "../user/courses/course.service";
import {CourseModel} from "../user/courses/models/course-model";
import {Calendar, CalendarOptions, EventClickArg} from "@fullcalendar/core";
import {ScheduledAppointmentModel} from "../user/courses/models/scheduled-appointment-model";
import {FullCalendarComponent, FullCalendarModule} from "@fullcalendar/angular";
import {RRule} from "rrule";
import {MatCard, MatCardContent, MatCardHeader} from "@angular/material/card";

import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridDay from '@fullcalendar/timegrid';
import rrulePlugin from '@fullcalendar/rrule'
import interactionPlugin from '@fullcalendar/interaction';
import {AccessibilityService} from "../accessibility.service";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-timetable',
  standalone: true,
    imports: [
        FullCalendarModule,
        MatCardContent,
        NgIf,
        MatCard,
        MatCardHeader,
    ],
  templateUrl: './timetable.component.html',
  styleUrl: './timetable.component.scss'
})
export class TimetableComponent implements OnInit{

    selectedEventDescription: string = '';

    @ViewChild('calendar') calendarComponent?: FullCalendarComponent;
    private readonly _calendarOptions: CalendarOptions = {
        plugins: [dayGridPlugin, timeGridDay, interactionPlugin, rrulePlugin],
        initialView: this.accessibilityService.mobile ? 'dayGridDay' : 'dayGridMonth',
        firstDay: 1,
        events: [], // to be altered when loaded
        selectable: true,
        eventClick: this.handleEventClick.bind(this),
        headerToolbar: {
            left: 'dayGridDay,dayGridWeek,dayGridMonth',
            center: 'title',
            right: 'prev,next'
        },
        height: 700,
        eventTimeFormat: {
            hour: '2-digit', minute: '2-digit', hour12: false
        },
    };

    handleEventClick(info: EventClickArg): void {
        this.selectedEventDescription = info.event.extendedProps['description'] || 'No description available';
    }

    constructor(private courseService: CourseService, private accessibilityService: AccessibilityService) {}

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
                        extendedProps: {
                            description: `test ahahaha ${name}`,
                        },
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

    private click(event: any): void {

    }
}
