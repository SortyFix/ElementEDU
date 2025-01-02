import {Component, input, InputSignal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {DateTimePickerComponent} from "../date-time-picker/date-time-picker.component";
import {DurationPickerComponent, DurationType} from "../duration-picker/duration-picker.component";
import {GeneralSelectionInput} from "../general-selection-input/general-selection-input.component";
import {RoomModel} from "../../../user/courses/room/room-model";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-create-standalone-appointment',
  standalone: true,
    imports: [
        DateTimePickerComponent,
        ReactiveFormsModule,
        MatTabGroup,
        MatTab,
        DurationPickerComponent,
        GeneralSelectionInput,
        NgIf
    ],
  templateUrl: './create-standalone-appointment.component.html',
  styleUrl: './create-standalone-appointment.component.scss'
})
export class CreateStandaloneAppointmentComponent {

    public readonly rooms: InputSignal<RoomModel[]> = input<RoomModel[]>([]);
    protected readonly DurationType: typeof DurationType = DurationType;
    private readonly _form: FormGroup;

    constructor(private formBuilder: FormBuilder) {
        const date: Date = new Date();
        this._form = this.formBuilder.group({
            start: [date, Validators.required],
            end: [null /* will be set below */, Validators.required],
            room: [null], // optional
            duration: [2700000, Validators.required],
        });

        this.updateEndBasedOnDuration();
        this.setupSync();
    }

    private setupSync(): void {
        this._form.get('start')?.valueChanges.subscribe((): void => { this.updateEndBasedOnDuration(); });
        this._form.get('duration')?.valueChanges.subscribe((): void => { this.updateEndBasedOnDuration(); });
        this._form.get('end')?.valueChanges.subscribe((): void => { this.updateDurationBasedOnEnd(); });
    }

    private updateEndBasedOnDuration(): void {

        if(this.isNull('start') || this.isNull('duration'))
        {
            return;
        }

        const start: number  = (this.form.get('start')?.value as Date).getTime();
        const duration: number = (this.form.get('duration')?.value as number);

        if (start && duration && duration > 0) {
            // Avoid triggering another update with (emit false)
            this.form.get('end')?.setValue(new Date(start + duration), { emitEvent: false });
        }
    }

    private updateDurationBasedOnEnd(): void {

        if(this.isNull('start') || this.isNull('end'))
        {
            return;
        }

        const start: number = (this._form.get('start')?.value as Date).getTime();
        const end: number = (new Date(this._form.get('end')?.value as Date)).getTime();

        if (start && end && end > start) {
            // Avoid triggering another update with (emit false)
            this.form.get('duration')?.setValue(end - start, { emitEvent: false });
        }
    }

    private isNull(attribute: string): boolean
    {
        return !this.form.get(attribute)?.value;
    }

    public get form(): FormGroup {
        return this._form;
    }
}
