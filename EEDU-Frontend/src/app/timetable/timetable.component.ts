import {Component, OnInit} from '@angular/core';
import {CourseService} from "../user/courses/course.service";
import {CourseModel} from "../user/courses/models/course-model";
import {CalendarOptions, EventInput} from "@fullcalendar/core";
import {ScheduledAppointmentModel} from "../user/courses/models/scheduled-appointment-model";
import {FullCalendarModule} from "@fullcalendar/angular";
import dayGridPlugin from '@fullcalendar/daygrid';

@Component({
  selector: 'app-timetable',
  standalone: true,
    imports: [
        FullCalendarModule
    ],
  templateUrl: './timetable.component.html',
  styleUrl: './timetable.component.scss'
})
export class TimetableComponent implements OnInit{

    events: EventInput[] = [];
    calendarOptions: CalendarOptions = {
        plugins: [dayGridPlugin],
        initialView: 'dayGridMonth',
        events: this.events,
    };

    constructor(private courseService: CourseService) {}

    ngOnInit(): void {
        this.courseService.fetchCourses().subscribe((value: CourseModel[]) => {
            value.forEach(v => {
                v.appointments.forEach(a => {
                    this.toEvents(v.name, a).forEach(g => {
                        this.events.push(g);
                    })
                })
            })
        })
    }

    private toEvents(title: string, appointment: ScheduledAppointmentModel): EventInput[]
    {
        const eventdd: EventInput[] = [];
        const startDate = new Date(Number(appointment.start));
        for (let i = 0; i < 52; i++) { // Assume 1 year of weekly repetition
            eventdd.push({
                title: title,
                start: new Date(startDate.getTime() + i * Number(appointment.period)),
                end: new Date(startDate.getTime() + i * Number(appointment.period) + Number(appointment.duration)),
            });
        }
        return eventdd;
    }
}
