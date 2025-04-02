import {Component} from '@angular/core';
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {CalendarView} from "angular-calendar";
import {MatIcon} from "@angular/material/icon";
import {FormsModule} from "@angular/forms";
import {MatIconButton} from "@angular/material/button";
import {UserService} from "../../user/user.service";
import {NgIf} from "@angular/common";
import {MatDialog} from "@angular/material/dialog";
import {MatChip} from "@angular/material/chips";
import {
    CreateAppointmentComponent
} from "../../user/courses/appointment/create-appointment/create-appointment.component";
import {AccountType} from "../../user/account-type";

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

    protected readonly CalendarView: typeof CalendarView = CalendarView;

    constructor(private _userService: UserService, private _dialog: MatDialog) {}

    private _viewDate: Date = new Date();

    public get viewDate(): Date {
        return this._viewDate;
    }

    public set viewDate(value: Date) {
        this._viewDate = value;
    }

    private _viewType: CalendarView = CalendarView.Month;

    public get viewType(): CalendarView {
        return this._viewType;
    }

    protected set viewType(value: CalendarView) {
        this._viewType = value;
    }

    public set dayClicked(date: Date) {
        if (this._viewType !== CalendarView.Month) {
            return;
        }

        this.viewType = CalendarView.Day;
        this.viewDateExact = date;
    }

    protected get isTeacher(): boolean {
        return this._userService.getUserData.accountType == AccountType.TEACHER;
    }

    protected get title(): string {
        switch (this._viewType) {
            case CalendarView.Month:
                return this.viewDate.toLocaleDateString('en-GB', {month: 'long', year: 'numeric'});

            case CalendarView.Day:
                return this.viewDate.toLocaleDateString('en-GB', {
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

                return `${startOfWeek.toLocaleDateString('en-GB', {
                    month: 'short',
                    day: 'numeric'
                })} – ${endOfWeek.toLocaleDateString('en-GB', {month: 'short', day: 'numeric', year: 'numeric'})}`;
        }
    }

    private set viewDateExact(value: Date) {
        this._viewDate = value;
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

    protected next(): void {
        this.moveView = 1;
    }

    protected previous(): void {
        this.moveView = -1;
    }

    protected createAppointment(): void {
        this._dialog.open(CreateAppointmentComponent, {
            width: '600px',
            disableClose: true,
        })
    }
}

