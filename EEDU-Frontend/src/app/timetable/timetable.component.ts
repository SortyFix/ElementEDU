import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {CourseService} from "../user/courses/course.service";
import {CourseModel} from "../user/courses/models/course-model";
import {CalendarOptions, EventInput} from "@fullcalendar/core";
import {ScheduledAppointmentModel} from "../user/courses/models/scheduled-appointment-model";
import {FullCalendarModule} from "@fullcalendar/angular";
import dayGridPlugin from '@fullcalendar/daygrid';
import {UserService} from "../user/user.service";
import {ThemeModel} from "../theming/theme-model";

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

    private _calendarOptions: CalendarOptions = {
        plugins: [dayGridPlugin],
        initialView: 'dayGridMonth',
        eventTimeFormat: {
            hour: '2-digit', minute: '2-digit', hour12: false
        },
        eventDidMount: (info) => {
            const theme: ThemeModel = this.userService.getUserData.theme;
            info.el.style.color = theme.getTextColor('widget', false);
            info.el.style.backgroundColor = theme.getBackgroundColor;
        },
        events: []
    };

    constructor(private userService: UserService, private courseService: CourseService, private changeDetector: ChangeDetectorRef) {}

    ngOnInit(): void {
        this.courseService.fetchCourses().subscribe((value: CourseModel[]) => {
            this._calendarOptions = {
                ...this._calendarOptions,
                events: value.flatMap((model: CourseModel): EventInput[] => {
                    const name: string = model.name;
                    return model.appointments.flatMap((scheduledModel: ScheduledAppointmentModel): EventInput[] => {
                        return this.toEvents(name, scheduledModel);
                    });
                })
            };

            this.changeDetector.detectChanges();
        });
    }

    private toEvents(title: string, appointment: ScheduledAppointmentModel): EventInput[]
    {
        const generatedEvents: EventInput[] = [];
        const rootDate = new Date(Number(appointment.start) * 1000);

        for (let i = 0; i < 52; i++) { // Assume 1 year of weekly repetition
            const currentStart = new Date(rootDate.getTime() + (i * (Number(appointment.period) * 1000)))
            const currentEnd = new Date(currentStart.getTime() + (Number(appointment.duration) * 1000))
            generatedEvents.push({
                title: title, start: currentStart, end: currentEnd,
            });
        }
        return generatedEvents;
    }

    protected get calendarOptions(): CalendarOptions {
        return this._calendarOptions;
    }
}
