import {Component, OnInit, ViewChild} from '@angular/core';
import {CourseService} from "../user/courses/course.service";
import {CourseModel} from "../user/courses/models/course-model";
import {Calendar, CalendarOptions, EventClickArg} from "@fullcalendar/core";
import {ScheduledAppointmentModel} from "../user/courses/models/scheduled-appointment-model";
import {FullCalendarComponent, FullCalendarModule} from "@fullcalendar/angular";
import {RRule} from "rrule";
import {MatDialog} from "@angular/material/dialog";
import {EventDialogComponent} from "./event-dialog/event-dialog.component";

import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridDay from '@fullcalendar/timegrid';
import rrulePlugin from '@fullcalendar/rrule'
import interactionPlugin from '@fullcalendar/interaction';
import {MatList, MatListItem, MatListItemTitle} from "@angular/material/list";

@Component({
  selector: 'app-timetable',
  standalone: true,
    imports: [
        FullCalendarModule,
        MatList,
        MatListItem,
        MatListItemTitle,
    ],
  templateUrl: './timetable.component.html',
  styleUrl: './timetable.component.scss'
})
export class TimetableComponent implements OnInit{

    private convert: (date: Date) => string = function (date: Date) {
        return `${date.getHours()}:${date.getMinutes()}:${date.getSeconds()}`;
    };

    private readonly _currentDate: Date = new Date();
    private readonly _endDate: Date = new Date(this._currentDate.getTime() + (5 * 60 * 60 * 1000));

    @ViewChild('calendar') calendarComponent?: FullCalendarComponent;
    private readonly _calendarOptions: CalendarOptions = {
        plugins: [dayGridPlugin, timeGridDay, interactionPlugin, rrulePlugin],
        initialView: 'timeGridWeek', // Change view on smaller screens
        firstDay: 1,
        events: [], // to be altered when loaded
        selectable: true,
        eventClick: this.handleEventClick.bind(this),
        headerToolbar: {
            left: '',
            right: ''
        },
        slotMinTime: this.convert(this._currentDate),
        slotMaxTime: this._currentDate.getDay() === this._endDate.getDay() ? this.convert(this._endDate) : '23:59:59',
        /*        headerToolbar: {
                    right: 'prev,next'
                },
                footerToolbar: {
                    center: 'dayGridMonth,timeGridWeek,timeGridDay',
                },*/
        footerToolbar: {
            right: 'prev,next',
        },
        height: 'auto', // Makes the calendar height adaptive to content
        contentHeight: 'auto',
        aspectRatio: 1.35,
        slotLabelFormat: {
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
        },
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
        private dialog: MatDialog) {}


    ngOnInit(): void {

        this.courseService.fetchCourses().subscribe((courses: CourseModel[]): void => {
            const api: Calendar = this.calendarComponent!.getApi();

            courses.forEach(({ name, scheduledAppointments }: CourseModel): void =>
                scheduledAppointments.forEach(({ start, end, period }: ScheduledAppointmentModel): void => {
                    const startDate: string = new Date(Number(start) * 1000).toISOString();
                    const endDate: string = new Date(Number(end) * 1000).toISOString();

                    api.addEvent({
                        title: name,
                        start: startDate,
                        end: endDate,
                        rrule: {
                            freq: RRule.MINUTELY,
                            interval: Number(period) / 60,
                            dtstart: startDate,
                            until: endDate,
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
