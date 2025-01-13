import {Component, input, InputSignal, OnChanges, SimpleChange, SimpleChanges} from '@angular/core';
import {CalendarEvent} from "angular-calendar";
import {FormBuilder, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {AppointmentService} from "../../user/courses/appointment/appointment.service";
import {AppointmentUpdateModel} from "../../user/courses/appointment/entry/appointment-update-model";
import {AssignmentCreateModel} from "../../user/courses/appointment/entry/assignment-create-model";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {
    DateTimePickerComponent
} from "../../user/courses/appointment/create-appointment/date-time-picker/date-time-picker.component";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {AppointmentEntryModel} from "../../user/courses/appointment/entry/appointment-entry-model";

@Component({
  selector: 'app-update-event',
  standalone: true,
    imports: [
        ReactiveFormsModule,
        MatLabel,
        MatFormField,
        DateTimePickerComponent,
        MatInput,
        MatButton
    ],
  templateUrl: './update-event.component.html',
  styleUrl: './update-event.component.scss'
})
export class UpdateEventComponent implements OnChanges {

    public readonly event: InputSignal<CalendarEvent | undefined> = input();
    private readonly _form: FormGroup;

    public ngOnChanges(changes: SimpleChanges) {
        const change: SimpleChange = changes['event'];
        if(change) {
            const eventData: AppointmentEntryModel = change.currentValue.meta.eventData;
            console.log("set description");
            this._form.get('description')?.setValue(eventData.description);
        }
    }

    public constructor(formBuilder: FormBuilder, private _appointmentService: AppointmentService)
    {
        this._form = formBuilder.group({
            description: [null],
            room: [null],
            assignment: [null],
            assignmentPublish: [new Date()],
            assignmentDeadline: [new Date(new Date().getTime() + 1000 * 60 * 60 * 24 * 7)]
        });
    }

    protected onSubmit(): void
    {
        const eventId: string | number | undefined = this.event()?.id;
        if(typeof eventId !== 'number')
        {
            return;
        }

        const updateModel: AppointmentUpdateModel = AppointmentUpdateModel.fromObject({
            description: this._form.get('description')?.value,
            assignment: new AssignmentCreateModel(
                this._form.get("assignment")?.value,
                this._form.get('assignmentPublish')?.value,
                this._form.get('assignmentDeadline')?.value
            )
        });

        this._appointmentService.updateAppointment(eventId, updateModel).subscribe()
    }

    protected get form(): FormGroup {
        return this._form;
    }
}
