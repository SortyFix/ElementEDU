import {Component, input, Input, InputSignal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {AppointmentService} from "../../user/courses/appointment/appointment.service";
import {AppointmentUpdateModel} from "../../user/courses/appointment/entry/appointment-update-model";
import {AppointmentEntryModel} from "../../user/courses/appointment/entry/appointment-entry-model";
import {RoomService} from "../../user/courses/room/room.service";
import {RoomModel} from "../../user/courses/room/room-model";
import {NgIf} from "@angular/common";
import {
    MatCard,
    MatCardActions,
    MatCardContent,
    MatCardHeader,
    MatCardSubtitle,
    MatCardTitle
} from "@angular/material/card";
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";
import {MatFormField, MatHint} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {GeneralSelectionInput} from "../general-selection-input/general-selection-input.component";
import {AssignmentModel} from "../../user/courses/appointment/entry/assignment-model";
import {GenericAssignmentCreateModel} from "../../user/courses/appointment/entry/assignment-create-model";
import {AssignmentTabComponent} from "./assignment-tab/assignment-tab.component";
import {
    DateTimePickerComponent
} from "../../user/courses/appointment/create-appointment/date-time-picker/date-time-picker.component";
import {EventTileContentComponent} from "./event-tile-content/event-tile-content.component";
import {RoomTabComponent} from "./room-tab/room-tab.component";

@Component({
  selector: 'app-update-event',
  standalone: true,
    imports: [
        ReactiveFormsModule,
        NgIf,
        MatCard,
        MatCardTitle,
        MatCardSubtitle,
        MatCardContent,
        MatCardHeader,
        MatHint,
        MatGridList,
        MatGridTile,
        MatIcon,
        MatIcon,
        MatFormField,
        MatInput,
        MatCardActions,
        MatButton,
        GeneralSelectionInput,
        AssignmentTabComponent,
        DateTimePickerComponent,
        EventTileContentComponent,
        RoomTabComponent
    ],
  templateUrl: './update-event.component.html',
  styleUrl: './update-event.component.scss'
})
export class UpdateEventComponent {

    public title: InputSignal<string> = input<string>('');
    public subtitle: InputSignal<string> = input<string>('');

    private _event!: AppointmentEntryModel;
    private readonly _form: FormGroup;
    private readonly _rooms: RoomModel[] = [];

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
            publish: [null],
            submitUntil: [null]
        });
    }

    @Input() public set appointment(value: AppointmentEntryModel) {
        this._event = value;

        this.form.get('description')?.setValue(this.event.description);
        this.form.get('room')?.setValue(this.event.room);

        if(value.assignment)
        {
            const assignment: AssignmentModel = value.assignment;
            this.form.get('assignment')?.setValue(assignment.description);
            this.form.get('publish')?.setValue(assignment.publish);
            this.form.get('submitUntil')?.setValue(assignment.submitUntil);
            return;
        }

        // Default values when creating a new assignment
        this.form.get('publish')?.setValue(new Date());

        // TODO next appointment
        this.form.get('submitUntil')?.setValue(new Date(new Date().getTime() + (1000 * 60 * 60 * 24 * 7)));

    }

    protected hasEdited(field: 'description' | 'room' | 'assignment'): boolean
    {
        switch (field)
        {
            case 'description': return this.form.get('description')?.value !== this.event.description;
            case 'room': return this.form.get('room')?.value !== this.event.room;
            case 'assignment':
                const assignment: AssignmentModel | undefined = this.event.assignment;
                if(!assignment)
                {
                    return !!this.form.get('assignment')?.value;
                }

                const assignmentEdited: boolean = this.form.get('assignment')?.value !== assignment.description;
                const publishEdited: boolean = (this.form.get('publish')?.value as Date).getTime() !== assignment.publish.getTime();
                const submitUntilEdited: boolean = (this.form.get('submitUntil')?.value as Date).getTime() !== assignment.submitUntil.getTime();

                return assignmentEdited || publishEdited || submitUntilEdited;
        }
    }

    protected get anyEdit(): boolean {
        return this.hasEdited('description') || this.hasEdited('room') || this.hasEdited('assignment')
    }

    protected onSubmit(): void
    {
        this._appointmentService.updateAppointment(this.event.id, AppointmentUpdateModel.fromObject({
            description: this.form.get('description')?.value,
            room: this.form.get('room')?.value?.id,
            // undefined means not updating  !!!
            assignment: this.hasEdited('assignment') ? this.assignmentCreateModel : undefined
        })).subscribe((response: AppointmentEntryModel): void => { this.appointment = response;  });
    }

    protected get event(): AppointmentEntryModel {
        return this._event;
    }

    protected get rooms(): RoomModel[] {
        return this._rooms;
    }

    protected get form(): FormGroup {
        return this._form;
    }

    private get assignmentCreateModel(): GenericAssignmentCreateModel {
        return {
            description: this.form.get("assignment")?.value,
            // if the 'assignment' field is undefined, these will be ignored
            publish: (this.form.get('publish')?.value as Date),
            submitUntil: (this.form.get('submitUntil')?.value as Date)
        }
    }

    protected get assignmentModel(): AssignmentModel {
        return AssignmentModel.fromObject({
            description: this.form.get('assignment')?.value,
            publish: this.form.get('publish')?.value,
            submitUntil: this.form.get('submitUntil')?.value
        });
    }
}
