import {Component, OnInit, ViewChild} from '@angular/core';
import {CourseService} from "../user/courses/course.service";
import {CourseModel} from "../user/courses/models/course-model";
import {Calendar, CalendarOptions, EventClickArg, EventInput} from "@fullcalendar/core";
import {ScheduledAppointmentModel} from "../user/courses/models/scheduled-appointment-model";
import {FullCalendarComponent, FullCalendarModule} from "@fullcalendar/angular";
import {MatDialog} from "@angular/material/dialog";
import {EventDialogComponent} from "./event-dialog/event-dialog.component";
import {MatList, MatListItem, MatListItemLine, MatListItemTitle} from "@angular/material/list";
import {AccessibilityService} from "../accessibility.service";

import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridDay from '@fullcalendar/timegrid';
import rrulePlugin from '@fullcalendar/rrule'
import interactionPlugin from '@fullcalendar/interaction';
import listPlugin from '@fullcalendar/list';

@Component({
  selector: 'app-timetable',
  standalone: true,
    imports: [
        FullCalendarModule,
        MatList,
        MatListItem,
        MatListItemTitle,
        MatListItemLine,
    ],
  templateUrl: './timetable.component.html',
  styleUrl: './timetable.component.scss'
})
export class TimetableComponent implements OnInit{

    @ViewChild('calendar') calendarComponent?: FullCalendarComponent;
    private readonly _calendarOptions: CalendarOptions = {
        plugins: [dayGridPlugin, timeGridDay, interactionPlugin, listPlugin, rrulePlugin],
        initialView: 'timeGridWeek', // dayGridMonth,timeGridWeek,timeGridDay
        firstDay: 1,
        events: [], // to be altered when loaded
        selectable: true,
        eventClick: this.handleEventClick.bind(this),
        headerToolbar: {
            left: '',
            center: 'title',
            right: ''
        },
        footerToolbar: {
            right: 'prev,next',
        },
        slotLabelFormat: {
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
        },
        height: 'auto',
        contentHeight: 'auto',
        aspectRatio: 1.35,
        eventTimeFormat: {
            hour: '2-digit', minute: '2-digit', hour12: false
        },
        displayEventTime: false,
        slotDuration: '01:00:00',
        slotLabelInterval: '01:00:00',
        titleFormat: {
            month: 'long',
            year: 'numeric',
        }
    };

    private handleEventClick(info: EventClickArg): void {

        const eventDescription = info.event.extendedProps['description'] || 'This is a test description for the event.';

        this.dialog.open(EventDialogComponent, {
            data: { description: eventDescription },
        });
    }

    constructor(private courseService: CourseService, private dialog: MatDialog, protected accessibilityService: AccessibilityService) {}

    ngOnInit(): void {
        this.courseService.fetchCourses().subscribe((courses: CourseModel[]): void => {
            const api: Calendar = this.calendarComponent!.getApi();

            courses.forEach(({ name, entries, scheduledAppointments }: CourseModel): void =>
                scheduledAppointments.forEach((entity: ScheduledAppointmentModel): void => {
                    api.addEvent(entity.asEvent(name, entries));
                })
            );
        });
    }

    protected get calendarOptions(): CalendarOptions {
        return this._calendarOptions;
    }
}
