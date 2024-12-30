import { Component } from '@angular/core';
import {DateTimePickerComponent} from "../../date-time-picker/date-time-picker.component";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from "@angular/material/datepicker";
import {DurationPickerComponent, DurationType} from "../../duration-picker/duration-picker.component";
import {MatInput} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {AccessibilityService} from "../../../accessibility.service";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-create-scheduled-appointment',
  standalone: true,
    imports: [
        ReactiveFormsModule,
        MatLabel,
        MatDatepickerToggle,
        MatDatepicker,
        DurationPickerComponent,
        MatFormField,
        MatInput,
        MatDatepickerInput,
        DateTimePickerComponent,
        MatIcon,
        MatTooltip,
        NgIf,
        MatSuffix
    ],
  templateUrl: './create-scheduled-appointment.component.html',
  styleUrl: './create-scheduled-appointment.component.scss'
})
export class CreateScheduledAppointmentComponent {

    protected readonly DurationType: typeof DurationType = DurationType;
    private readonly _form: FormGroup;
    private readonly _date: Date;

    constructor(private readonly _accessibilityService: AccessibilityService, formBuilder: FormBuilder) {
        this._date = new Date();

        this._form = formBuilder.group({
            start: [this.date, Validators.required],
            until: [new Date(this.date.getTime() + 86400000), Validators.required],
            frequency: [604800000, Validators.required],
            duration: [2700000, Validators.required],
        });
    }

    public get form(): FormGroup {
        return this._form;
    }

    protected get isMobile(): boolean {
        return this._accessibilityService.mobile;
    }

    protected get date(): Date {
        return this._date;
    }
}
