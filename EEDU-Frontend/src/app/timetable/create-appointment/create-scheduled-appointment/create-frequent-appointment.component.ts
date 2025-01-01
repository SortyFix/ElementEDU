import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from "@angular/material/datepicker";
import {MatInput} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {AccessibilityService} from "../../../accessibility.service";
import {NgIf} from "@angular/common";
import {RoomService} from "../../../user/courses/room/room.service";
import {RoomModel} from "../../../user/courses/room/room-model";
import {DurationPickerComponent, DurationType} from "../duration-picker/duration-picker.component";
import {DateTimePickerComponent} from "../date-time-picker/date-time-picker.component";
import {GeneralSelectionInput} from "../general-selection-input/general-selection-input.component";

@Component({
  selector: 'app-create-frequent-appointment',
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
        MatSuffix,
        GeneralSelectionInput
    ],
  templateUrl: './create-frequent-appointment.component.html',
  styleUrl: './create-frequent-appointment.component.scss'
})
export class CreateFrequentAppointmentComponent {

    protected readonly DurationType: typeof DurationType = DurationType;
    private readonly _form: FormGroup;
    private readonly _date: Date;
    private _rooms: RoomModel[] = [];

    constructor(private readonly _accessibilityService: AccessibilityService, private readonly _roomService: RoomService, formBuilder: FormBuilder) {
        this._date = new Date();

        this._form = formBuilder.group({
            start: [this.date, Validators.required],
            until: [new Date(this.date.getTime() + 86400000), Validators.required],
            room: [undefined, Validators.required],
            duration: [2700000, Validators.required],
            frequency: [604800000, Validators.required],
        });

        this._roomService.fetchRooms().subscribe(((rooms: RoomModel[]): void => { this._rooms = rooms; }));
    }

    protected get rooms(): RoomModel[] {
        return this._rooms;
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
