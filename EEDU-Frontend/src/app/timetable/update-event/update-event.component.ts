import {Component, input, InputSignal} from '@angular/core';
import {CalendarEvent} from "angular-calendar";
import {FormBuilder, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {AppointmentService} from "../../user/courses/appointment/appointment.service";
import {AppointmentUpdateModel} from "../../user/courses/appointment/entry/appointment-update-model";
import {
    GenericAssignmentCreateModel
} from "../../user/courses/appointment/entry/assignment-create-model";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {
    DateTimePickerComponent
} from "../../user/courses/appointment/create-appointment/date-time-picker/date-time-picker.component";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {AppointmentEntryModel} from "../../user/courses/appointment/entry/appointment-entry-model";
import {RoomService} from "../../user/courses/room/room.service";
import {RoomModel} from "../../user/courses/room/room-model";
import {GeneralSelectionInput} from "../general-selection-input/general-selection-input.component";
import {MatList, MatListItem, MatListItemLine, MatListItemTitle} from "@angular/material/list";

@Component({
  selector: 'app-update-event',
  standalone: true,
    imports: [
        ReactiveFormsModule,
        MatLabel,
        MatFormField,
        DateTimePickerComponent,
        MatInput,
        MatButton,
        GeneralSelectionInput,
        MatListItem,
        MatList,
        MatListItemLine,
        MatListItemTitle,
    ],
  templateUrl: './update-event.component.html',
  styleUrl: './update-event.component.scss'
})
export class UpdateEventComponent {

    public readonly event: InputSignal<CalendarEvent | undefined> = input();
    private readonly _form: FormGroup;
    private readonly _rooms: RoomModel[] = [];
    private _editMode: boolean = false;

    public switchEditMode(): void {
        const event: CalendarEvent | undefined = this.event();
        if(!event)
        {
            return;
        }

        this._editMode = !this._editMode;
        const eventData: AppointmentEntryModel = event.meta.eventData;
        this._form.get('description')?.setValue(eventData.description);
        this._form.get('room')?.setValue(eventData.room);
    }

    public constructor(formBuilder: FormBuilder, roomService: RoomService, private _appointmentService: AppointmentService)
    {
        // automatically fetches the rooms
        roomService.rooms$.subscribe((rooms: RoomModel[]): void => {
            this._rooms.length = 0;
            this._rooms.push(...rooms);
        });

        this._form = formBuilder.group({
            description: [null],
            room: [null],
            assignment: [null],
            assignmentPublish: [new Date()],
            assignmentDeadline: [new Date(new Date().getTime() + 1000 * 60 * 60 * 24 * 7)]
        });
    }

    protected get rooms(): RoomModel[] {
        return this._rooms;
    }

    protected get form(): FormGroup {
        return this._form;
    }

    protected get editMode(): boolean {
        return this._editMode;
    }

    protected onSubmit(): void
    {
        if(!this.canSubmit() || !this.id) // must be doubled because typescript won't accept it
        {
            return;
        }

        this._appointmentService.updateAppointment(this.id, AppointmentUpdateModel.fromObject({
            description: this._form.get('description')?.value,
            assignment: this.assignmentCreateModel
        })).subscribe((): void => { this._editMode = false; });
    }

    protected canSubmit(): boolean {
        return this._editMode;
    }

    protected get id(): bigint | undefined {
        return this.event()?.meta.id;
    }

    private get assignmentCreateModel(): GenericAssignmentCreateModel {
        return {
            description: this._form.get("assignment")?.value,
            publish: this._form.get('assignmentPublish')?.value,
            submitUntil: this._form.get('assignmentDeadline')?.value
        }
    }
}
