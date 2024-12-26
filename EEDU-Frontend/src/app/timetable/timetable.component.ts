import {AfterViewInit, Component, Inject, model, ModelSignal, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {CourseService} from "../user/courses/course.service";
import {AccessibilityService} from "../accessibility.service";
import {
    CalendarDateFormatter,
    CalendarDayModule,
    CalendarEvent,
    CalendarModule,
    CalendarMonthModule, CalendarMonthViewDay,
    CalendarView,
    CalendarWeekModule,
} from "angular-calendar";
import {CourseModel} from "../user/courses/models/course-model";
import {DOCUMENT, NgIf} from "@angular/common";
import {MatList, MatListItem, MatListItemLine, MatListItemTitle} from "@angular/material/list";
import {FormsModule} from "@angular/forms";
import {CalendarControlsComponent} from "./calendar-controls/calendar-controls.component";
import {AppointmentEntryModel} from "../user/courses/models/appointments/appointment-entry-model";
import {ScheduledAppointmentModel} from "../user/courses/models/appointments/scheduled-appointment-model";
import {MatCalendar} from "@angular/material/datepicker";
import {MatDivider} from "@angular/material/divider";
import {DateFormatter} from "./date-formatter";
import {MatButton} from "@angular/material/button";


@Component({
  selector: 'app-timetable',
  standalone: true,
    imports: [
        CalendarMonthModule,
        CalendarWeekModule,
        CalendarDayModule,
        CalendarModule,
        NgIf,
        MatList,
        MatListItem,
        MatListItemTitle,
        MatListItemLine,
        FormsModule,
        CalendarControlsComponent,
        MatCalendar,
        MatDivider,
        MatButton
    ],
    providers: [
        {
            provide: CalendarDateFormatter,
            useClass: DateFormatter
        }
    ],
  templateUrl: './timetable.component.html',
  styleUrl: './timetable.component.scss'
})
export class TimetableComponent implements OnInit, AfterViewInit, OnDestroy {

    @ViewChild('controls') controls!: CalendarControlsComponent;
    selected: ModelSignal<Date | null> = model<Date | null>(null);
    events: CalendarEvent[] = [];

    onDayClicked(event: CalendarMonthViewDay): void {
        if(this.controls)
        {
            this.controls.dayClicked = event.date;
        }
    }

    protected readonly wrapSize: number = 1200;

    protected get screenWidth(): number {
        return this._accessibilityService.dimensions.width;
    }

    constructor(private _courseService: CourseService, private _accessibilityService: AccessibilityService, @Inject(DOCUMENT) private document: any) {}

    public ngOnInit(): void {
        this.document.body.classList.add(this.calendarThemeClass)
        this._courseService.fetchCourses().subscribe((): void  => { this.events = this.coursesToEvents(); });
    }

    protected toString(date: Date): string {
        return date.toLocaleDateString('de-DE', {
            day: 'numeric',
            month: 'numeric',
            year: 'numeric'
        });
    }

    private coursesToEvents(): CalendarEvent[] {
        return this._courseService.courses.flatMap(
            ({ name, appointmentEntries, scheduledAppointments }: CourseModel): CalendarEvent[] => [
                ...this.toEvents(name, scheduledAppointments),
                ...appointmentEntries.map((entity: AppointmentEntryModel): CalendarEvent => entity.asEvent(name)),
            ]
        );
    }

    ngAfterViewInit() {
        this.selected.set(this.controls.viewDate)
    }

    private readonly calendarThemeClass: string = 'calendar-theme';

    public ngOnDestroy(): void {
        this.document.body.classList.remove(this.calendarThemeClass)
    }


    /**
     * Converts an array of {@link ScheduledAppointmentModel} into an array of {@link CalendarEvent}.
     *
     * This method processes an array of {@link ScheduledAppointmentModel} instances and transforms each
     * into one or more {@link CalendarEvent} objects. It achieves this by iterating over the array,
     * invoking the #asEvent() method on each {@link ScheduledAppointmentModel}, and then using a
     * flat-mapping operation to combine the results into a single array.
     *
     * @param name - A descriptive name for the operation or transformation process.
     * @param scheduled - An array of {@link ScheduledAppointmentModel} instances to be converted.
     * @returns An array of {@link CalendarEvent} instances derived from the input models.
     * @private
     */
    private toEvents(name: string, scheduled: ScheduledAppointmentModel[]): CalendarEvent[] {
        return scheduled.flatMap((entity: ScheduledAppointmentModel): CalendarEvent[] => entity.asEvent(name));
    }

    protected readonly CalendarView: typeof CalendarView = CalendarView;
}
