import {Component, input, InputSignal} from '@angular/core';
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {CalendarView} from "angular-calendar";
import {MatIcon} from "@angular/material/icon";
import {FormsModule} from "@angular/forms";
import {MatIconButton} from "@angular/material/button";
import {UserService} from "../../user/user.service";
import {NgIf} from "@angular/common";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {CreateAppointmentComponent} from "../create-appointment/create-appointment.component";
import {CourseModel} from "../../user/courses/models/course-model";
import {MatChip, MatChipSet} from "@angular/material/chips";

@Component({
  selector: 'app-calendar-controls',
  standalone: true,
    imports: [
        MatButtonToggleGroup,
        MatButtonToggle,
        MatIcon,
        FormsModule,
        MatIconButton,
        NgIf,
        MatChip,
    ],
  templateUrl: './calendar-controls.component.html',
  styleUrl: './calendar-controls.component.scss'
})
export class CalendarControlsComponent {

    private _viewDate: Date = new Date();
    private _viewType: CalendarView = CalendarView.Month;

    constructor(private _userService: UserService, private _dialog: MatDialog) {}

    protected get isTeacher(): boolean {
        return this._userService.getUserData.inGroup('teacher');
    }

    protected set viewType(value: CalendarView) {
        this._viewType = value;
    }

    public set dayClicked(date: Date)
    {
        if (this._viewType !== CalendarView.Month) {
            return;
        }

        this.viewType = CalendarView.Day;
        this.viewDateExact = date;
    }

    public get viewType(): CalendarView {
        return this._viewType;
    }

    public get viewDate(): Date {
        return this._viewDate;
    }

    public set viewDate(value: Date) {
        this._viewDate = value;
    }


    private set viewDateExact(value: Date) {
        this._viewDate = value;
    }

    protected next(): void {
        this.moveView = 1;
    }

    protected previous(): void {
        this.moveView = -1;
    }

    private set moveView(steps: number) {
        const currentDate: Date = this.viewDate;
        switch (this._viewType) {
            case CalendarView.Month:
                this.viewDateExact = new Date(currentDate.getFullYear(), currentDate.getMonth() + steps, 1);
                break;
            case CalendarView.Week:
                this.viewDateExact = new Date(currentDate.getFullYear(), currentDate.getMonth(), currentDate.getDate() + (steps * 7));
                break;
            case CalendarView.Day:
                this.viewDateExact = new Date(currentDate.getFullYear(), currentDate.getMonth(), currentDate.getDate() + steps);
                break;
        }
    }

    protected get title(): string {
        switch (this._viewType) {
            case CalendarView.Month:
                return this.viewDate.toLocaleDateString('de-DE', {month: 'long', year: 'numeric'});

            case CalendarView.Day:
                return this.viewDate.toLocaleDateString('de-DE', {
                    weekday: 'long',
                    month: 'long',
                    day: 'numeric',
                    year: 'numeric'
                });

            case CalendarView.Week:
                const startOfWeek = new Date(this.viewDate);
                const endOfWeek = new Date(this.viewDate);

                const dayOfWeek = startOfWeek.getDay();
                const daysToMonday = (dayOfWeek === 0 ? -6 : 1) - dayOfWeek; // Adjust to Monday
                startOfWeek.setDate(this.viewDate.getDate() + daysToMonday); // Go to Monday
                endOfWeek.setDate(startOfWeek.getDate() + 6); // Go to Sunday (end of the week)

                return `${startOfWeek.toLocaleDateString('de-DE', {
                    month: 'short',
                    day: 'numeric'
                })} – ${endOfWeek.toLocaleDateString('de-DE', {month: 'short', day: 'numeric', year: 'numeric'})}`;
        }
    }

    protected createAppointment(): void
    {
        const dialogRef: MatDialogRef<CreateAppointmentComponent> = this._dialog.open(CreateAppointmentComponent, {
            width: '600px',
            disableClose: true,
        })
    }

    protected readonly CalendarView: typeof CalendarView = CalendarView;
}

