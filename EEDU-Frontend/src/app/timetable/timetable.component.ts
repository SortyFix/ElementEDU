import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {CourseService} from "../user/courses/course.service";
import {AccessibilityService} from "../accessibility.service";
import {
    CalendarDateFormatter,
    CalendarDayModule,
    CalendarEvent,
    CalendarMonthModule,
    CalendarWeekModule, DateFormatterParams
} from "angular-calendar";
import {CourseModel} from "../user/courses/models/course-model";
import {ScheduledAppointmentModel} from "../user/courses/models/scheduled-appointment-model";
import {DOCUMENT, NgIf} from "@angular/common";
import {MatList, MatListItem, MatListItemLine, MatListItemTitle} from "@angular/material/list";
import {AppointmentEntryModel} from "../user/courses/models/appointment-entry-model";
import {getISOWeek} from "date-fns";

@Component({
  selector: 'app-timetable',
  standalone: true,
    imports: [
        CalendarMonthModule,
        CalendarWeekModule,
        CalendarDayModule,
        NgIf,
        MatList,
        MatListItem,
        MatListItemTitle,
        MatListItemLine
    ],
  templateUrl: './timetable.component.html',
  styleUrls: ['./timetable.component.scss']
})
export class TimetableComponent implements OnInit, OnDestroy {

    viewDate: Date = new Date();
    events: CalendarEvent[] = [];

    onDayClicked(event: any): void {
        console.log('Day clicked', event);
    }

    protected readonly wrapSize: number = 1200;

    protected get screenWidth(): number {
        return this._accessibilityService.dimensions.width;
    }

    constructor(private _courseService: CourseService, private _accessibilityService: AccessibilityService, @Inject(DOCUMENT) private document: any) {}

    public ngOnInit(): void {
        this.document.body.classList.add(this.darkThemeClass)
        this._courseService.fetchCourses().subscribe((courses: CourseModel[]): void => {
            courses.forEach(({ name, appointmentEntries, scheduledAppointments }: CourseModel): void => {
                this.events.push(
                    ...this.toEvents(name, scheduledAppointments),
                    ...appointmentEntries.map((entity: AppointmentEntryModel): CalendarEvent => entity.asEvent(name))
                );
            });
        });
    }
    private readonly darkThemeClass = 'calendar-theme';

    public ngOnDestroy(): void {
        this.document.body.classList.remove(this.darkThemeClass)
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
}
