import {Component, OnInit, ViewChild} from '@angular/core';
import {CourseService} from "../user/courses/course.service";
import {CourseModel} from "../user/courses/models/course-model";
import {Calendar, CalendarOptions, EventClickArg} from "@fullcalendar/core";
import {ScheduledAppointmentModel} from "../user/courses/models/scheduled-appointment-model";
import {FullCalendarComponent, FullCalendarModule} from "@fullcalendar/angular";
import {RRule} from "rrule";
import {AccessibilityService} from "../accessibility.service";
import {MatDialog} from "@angular/material/dialog";
import {EventDialogComponent} from "./event-dialog/event-dialog.component";

import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridDay from '@fullcalendar/timegrid';
import rrulePlugin from '@fullcalendar/rrule'
import interactionPlugin from '@fullcalendar/interaction';

@Component({
  selector: 'app-timetable',
  standalone: true,
    imports: [
        FullCalendarModule,
    ],
  templateUrl: './timetable.component.html',
  styleUrl: './timetable.component.scss'
})
export class TimetableComponent implements OnInit{

    @ViewChild('calendar') calendarComponent?: FullCalendarComponent;
    private readonly _calendarOptions: CalendarOptions = {
        plugins: [dayGridPlugin, timeGridDay, interactionPlugin, rrulePlugin],
        initialView: window.innerWidth < 768 ? 'dayGridDay' : 'dayGridMonth', // Change view on smaller screens
        firstDay: 1,
        events: [], // to be altered when loaded
        selectable: true,
        eventClick: this.handleEventClick.bind(this),
        headerToolbar: {
            right: 'prev,next'
        },
        footerToolbar: {
            center: 'dayGridMonth,timeGridWeek,timeGridDay',
        },
        height: 'auto', // Makes the calendar height adaptive to content
        contentHeight: 'auto',
        aspectRatio: 1.35,
        eventTimeFormat: {
            hour: '2-digit', minute: '2-digit', hour12: false
        },
    };

    handleEventClick(info: EventClickArg): void {
        const eventDescription = info.event.extendedProps['description'] || 'This is a test description for the event.';
        this.dialog.open(EventDialogComponent, {
            data: { description: eventDescription },
            position: { top: `${info.jsEvent.pageY}px`, left: `${info.jsEvent.pageX}px` },
        });
    }

    constructor(
        private courseService: CourseService,
        private accessibilityService: AccessibilityService,
        private dialog: MatDialog) {}


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
                            description: `${timeStamp} - ${name}`,
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
}
