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
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatFormField, MatHint} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";

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
        MatIconButton,
        MatIcon,
        MatFormField,
        MatInput,
        MatCardActions,
        MatButton,
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

    private readonly _edit: {
        description: boolean,
        room: boolean,
        assignment: boolean
    } = { description: false, room: false, assignment: false }

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
            assignmentPublish: [null],
            submitUntil: [null]
        });
    }

    @Input() public set appointment(value: AppointmentEntryModel) {
        this._event = value;

        this.form.get('description')?.setValue(this.event.description);
        this.form.get('room')?.setValue(this.event.room);
        this.form.get('assignment')?.setValue(this.event.assignment?.description);
        this.form.get('submitUntil')?.setValue(this.event.assignment?.submitUntil);
    }

    protected get isCurrentlyEditing(): boolean {
        return this.isEditing('description') || this.isEditing('room') || this.isEditing('assignment');
    }

    protected get hasEdited(): boolean {
        return this.form.get('description')?.value !== this.event.description
        || this.form.get('room')?.value !== this.event.room
        || this.form.get('assignment')?.value !== this.event.assignment;
    }

    protected get enableScroll(): 'scroll' | null
    {
        return this.isEditing('description') ? null : 'scroll'
    }

    protected set switchEdit(current: 'description' | 'room' | 'assignment')
    {
        this._edit[current] = !this._edit[current];
    }

    protected onSubmit(): void
    {
        let roomId: number | undefined = undefined;
        const room: RoomModel | undefined = this.form.get('room')?.value;
        if(room)
        {
            roomId = room.id;
        }

        this._appointmentService.updateAppointment(this.event.id, AppointmentUpdateModel.fromObject({
            description: this.form.get('description')?.value,
            room: roomId,
            assignment: undefined
        })).subscribe((response: AppointmentEntryModel): void => { this.appointment = response;  });
    }

    protected isEditing(current: 'description' | 'room' | 'assignment'): boolean {
        return this._edit[current];
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

/*    private get assignmentCreateModel(): GenericAssignmentCreateModel {
        return {
            description: this.form.get("assignment")?.value,
            publish: (this.form.get('assignmentPublish')?.value as Date),
            submitUntil: (this.form.get('submitUntil')?.value as Date)
        }
    }*/

    protected readonly frameElement = frameElement;
}
