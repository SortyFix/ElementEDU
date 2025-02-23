import {Component, Inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {CourseService} from "../user/courses/course.service";
import {AccessibilityService} from "../accessibility.service";
import {
    CalendarDateFormatter,
    CalendarDayModule,
    CalendarEvent,
    CalendarModule,
    CalendarMonthModule,
    CalendarMonthViewDay,
    CalendarView,
    CalendarWeekModule,
} from "angular-calendar";
import {DOCUMENT, NgForOf, NgIf} from "@angular/common";
import {MatList, MatListItem, MatListItemLine, MatListItemTitle} from "@angular/material/list";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CalendarControlsComponent} from "./calendar-controls/calendar-controls.component";
import {MatCalendar} from "@angular/material/datepicker";
import {MatDivider} from "@angular/material/divider";
import {DateFormatter} from "./date-formatter";
import {MatButton} from "@angular/material/button";
import {Observable} from "rxjs";
import {MatDialog} from "@angular/material/dialog";
import {CourseModel} from "../user/courses/course-model";
import {AppointmentEntryModel} from "../user/courses/appointment/entry/appointment-entry-model";
import {FrequentAppointmentModel} from "../user/courses/appointment/frequent/frequent-appointment-model";
import {EventDataComponent} from "./event-data/event-data.component";
import {AppointmentService} from "../user/courses/appointment/appointment.service";
import {AppointmentCreateModel} from "../user/courses/appointment/entry/appointment-create-model";


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
        MatButton,
        NgForOf,
        ReactiveFormsModule,
        EventDataComponent
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
export class TimetableComponent implements OnInit, OnDestroy {

    @ViewChild('controls') controls!: CalendarControlsComponent;
    @ViewChild('eventComponent') eventComponent!: EventDataComponent;
    protected readonly AppointmentEntryModel = AppointmentEntryModel;
    protected readonly FrequentAppointmentModel = FrequentAppointmentModel;
    private readonly CALENDAR_THEME_CLASS: string = 'calendar-theme';
    private readonly _CalendarView: typeof CalendarView = CalendarView;

    constructor(
        private readonly _dialogRef: MatDialog,
        private readonly _courseService: CourseService,
        private readonly _appointmentService: AppointmentService,
        private readonly _accessibilityService: AccessibilityService,
        @Inject(DOCUMENT) private document: any,
    ) {}

    private _events: CalendarEvent[] = [];

    /**
     * Retrieves the list of {@link CalendarEvent} instances managed by this component.
     *
     * This accessor provides the array of {@link CalendarEvent} instances that represent the events currently
     * associated with the calendar. These events are used to populate and display the calendar's content.
     *
     * @returns an array of {@link CalendarEvent} instances.
     * @protected
     */
    protected get events(): CalendarEvent[] {
        return this._events;
    }

    private _selectedEvent?: CalendarEvent;

    protected get selectedEvent(): CalendarEvent | undefined {
        return this._selectedEvent;
    }

    private set selectedEvent(value: CalendarEvent | undefined) {
        this._selectedEvent = value;
    }

    protected get selectedAppointment(): AppointmentEntryModel {
        return this.selectedEvent?.meta.eventData;
    }

    protected get nextEvents(): CalendarEvent[] {
        const refDate: Date = new Date();
        return this.events.filter((event: CalendarEvent): boolean => event.start > refDate)
            .sort((a: CalendarEvent, b: CalendarEvent): number => a.start.getTime() - b.start.getTime())
            .slice(0, 3);
    }

    /**
     * Determines if wrapping is enabled based on the accessibility service's width.
     *
     * This accessor checks the width of the {@link AccessibilityService}'s dimensions and returns true if the width
     * is less than or equal to {@code 1200}. This is typically used for adjusting layout or UI behavior for smaller screens.
     *
     * @returns a boolean indicating whether wrapping is enabled.
     * @protected
     */
    protected get wrap(): boolean {
        return this._accessibilityService.dimensions.width <= 1200;
    }

    /**
     * Retrieves the {@link CalendarView} class used by this component.
     *
     * This accessor provides the {@link CalendarView} class, which is used to represent and manage the calendar view
     * within the component. It serves as a reference to the type of calendar view being utilized.
     *
     * @returns the {@link CalendarView} class.
     * @protected
     */
    protected get CalendarView(): typeof CalendarView {
        return this._CalendarView;
    }

    /**
     * Retrieves the {@link CourseService} instance used by the class.
     *
     * This accessor provides the instance of {@link CourseService} used by this class. It allows access to
     * the service used for operations related to courses and their data.
     *
     * @returns the {@link CourseService} instance used internally by this class.
     * @private
     */
    private get courseService(): CourseService {
        return this._courseService;
    }

    /**
     * Initializes the component and sets up the calendar theme and event subscriptions.
     *
     * This lifecycle method applies the calendar theme to the document body and subscribes to the courses
     * observable from the {@link CourseService}. When courses are fetched, it transforms them into events
     * using the {@link #courseEvents} method and stores them in the {@code _events} array. If courses haven't been
     * fetched yet, it triggers a fetch operation.
     *
     * @public
     */
    public ngOnInit(): void {
        document.body.classList.add(this.CALENDAR_THEME_CLASS);

        const courses: Observable<CourseModel[]> = this.courseService.value$;
        courses.subscribe((courses: CourseModel[]): void => { this._events = this.courseEvents(courses); });
    }

    /**
     * Removes the calendar theme from the document body.
     *
     * This lifecycle method is invoked when the component is destroyed. It ensures that the calendar theme
     * class is removed from the document body to prevent styling issues when the component is no longer in use.
     *
     * @public
     */
    public ngOnDestroy(): void {
        this.document.body.classList.remove(this.CALENDAR_THEME_CLASS)
    }

    protected createEvent(): void {
        if (!this.selectedEvent) {
            return;
        }

        const frequentData: FrequentAppointmentModel = this.selectedEvent.meta.eventData as FrequentAppointmentModel;
        this._appointmentService.createAppointment(frequentData.course.id, [AppointmentCreateModel.fromObject({
            start: this.selectedEvent.start,
            room: frequentData.room,
            duration: frequentData.duration,
        })]).subscribe((createdEvent: AppointmentEntryModel[]): void => {
            this.selectedEvent = this.events.find((current: CalendarEvent): boolean => {
                return typeof current.id === 'number' && BigInt(current.id) === createdEvent[0].id;
            });
        });
    }

    protected onDayClicked(event: CalendarMonthViewDay): void {
        this.controls.dayClicked = event.date;
    }

    protected onEventClicked(event: CalendarEvent): void {
        this.selectedEvent = event;
    }

    protected dateToString(date: Date): string {
        return date.toLocaleDateString('de-DE', {
            day: 'numeric',
            month: 'numeric',
            year: 'numeric'
        });
    }

    /**
     * Converts an array of {@link CourseModel} instances into an array of {@link CalendarEvent} instances.
     *
     * This method processes each {@link CourseModel} in the input array by extracting scheduled appointments and
     * appointment entries. It invokes the {@link #toEvents} method to transform scheduled appointments and maps
     * {@link AppointmentEntryModel} instances to {@link CalendarEvent} using their {@link #asEvent} method. The results
     * are combined using a flat mapping operation.
     *
     * @param courses an array of {@link CourseModel} instances containing appointment data to convert.
     * @returns an array of {@link CalendarEvent} instances derived from the courses' appointments and entries.
     * @private
     */
    private courseEvents(courses: CourseModel[]): CalendarEvent[] {
        return courses.flatMap(({name, appointmentEntries, frequentAppointments}: CourseModel): CalendarEvent[] => [
            ...this.toEvents(name, frequentAppointments),
            ...appointmentEntries.map((entity: AppointmentEntryModel): CalendarEvent => entity.asEvent(name)),
        ]);
    }

    /**
     * Converts an array of {@link FrequentAppointmentModel} into an array of {@link CalendarEvent}.
     *
     * This method processes an array of {@link FrequentAppointmentModel} instances and transforms each
     * into one or more {@link CalendarEvent} objects. It achieves this by iterating over the array,
     * invoking the #asEvent() method on each {@link FrequentAppointmentModel}, and then using a
     * flat-mapping operation to combine the results into a single array.
     *
     * @param name a descriptive name for the operation or transformation process.
     * @param scheduled an array of {@link FrequentAppointmentModel} instances to be converted.
     * @returns an array of {@link CalendarEvent} instances derived from the input models.
     * @private
     */
    private toEvents(name: string, scheduled: readonly FrequentAppointmentModel[]): CalendarEvent[] {
        return scheduled.flatMap((entity: FrequentAppointmentModel): CalendarEvent[] => entity.asEvent(name));
    }

}
