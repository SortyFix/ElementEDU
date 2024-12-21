import {Component, Output} from '@angular/core';
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {CalendarView} from "angular-calendar";
import {MatIcon} from "@angular/material/icon";
import {FormsModule} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatFormField, MatHint, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from "@angular/material/datepicker";

@Component({
  selector: 'app-calendar-controls',
  standalone: true,
    imports: [
        MatButtonToggleGroup,
        MatButtonToggle,
        MatIcon,
        FormsModule,
        MatIconButton,
        MatButton,
        MatFormField,
        MatInput,
        MatDatepickerInput,
        MatDatepickerToggle,
        MatDatepicker,
        MatHint,
        MatLabel
    ],
  templateUrl: './calendar-controls.component.html',
  styleUrl: './calendar-controls.component.scss'
})
export class CalendarControlsComponent {

    private _viewDate: Date = new Date();
    private _viewType: CalendarView = CalendarView.Month;

    protected set viewType(value: CalendarView) {
        this._viewType = value;
    }

    public get viewType(): CalendarView {
        return this._viewType;
    }

    public get viewDate(): Date {
        return this._viewDate;
    }


    private set viewDate(value: Date) {
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
                this.viewDate = new Date(currentDate.getFullYear(), currentDate.getMonth() + steps, 1);
                break;
            case CalendarView.Week:
                this.viewDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), currentDate.getDate() + (steps * 7));
                break;
            case CalendarView.Day:
                this.viewDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), currentDate.getDate() + steps);
                break;
        }
    }

    protected get title(): string {
        switch (this._viewType) {
            case CalendarView.Month:
                return this.viewDate.toLocaleDateString('en-US', {month: 'long', year: 'numeric'});

            case CalendarView.Day:
                return this.viewDate.toLocaleDateString('en-US', {
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

                return `${startOfWeek.toLocaleDateString('en-US', {
                    month: 'short',
                    day: 'numeric'
                })} – ${endOfWeek.toLocaleDateString('en-US', {month: 'short', day: 'numeric', year: 'numeric'})}`;
        }

        return ''

    }

    protected readonly CalendarView: typeof CalendarView = CalendarView;
}

