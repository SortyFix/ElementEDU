import {Component, Inject, Input} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {AppointmentService} from "../../user/courses/appointment/appointment.service";
import {AppointmentUpdateModel} from "../../user/courses/appointment/entry/appointment-update-model";
import {AppointmentEntryModel} from "../../user/courses/appointment/entry/appointment-entry-model";
import {RoomService} from "../../user/courses/room/room.service";
import {RoomModel} from "../../user/courses/room/room-model";
import {NgIf} from "@angular/common";
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {MatButton} from "@angular/material/button";
import {MatFormField, MatHint} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {AssignmentTabComponent} from "./assignment-tab/assignment-tab.component";
import {
    DateTimePickerComponent
} from "../../user/courses/appointment/create-appointment/date-time-picker/date-time-picker.component";
import {EventTileContentComponent} from "./event-tile-content/event-tile-content.component";
import {RoomTabComponent} from "./room-tab/room-tab.component";
import {SelectionInput} from "../../common/selection-input/selection-input.component";
import {
    MAT_DIALOG_DATA, MatDialogActions, MatDialogContent,
} from "@angular/material/dialog";
import {MatSnackBar} from "@angular/material/snack-bar";
import {CourseService} from "../../user/courses/course.service";
import {CourseModel} from "../../user/courses/course-model";
import {GeneralCardComponent} from "../../common/general-card-component/general-card.component";
import {AssignmentModel} from "../../user/courses/appointment/entry/assignment/assignment-model";
import {GenericAssignmentCreateModel} from "../../user/courses/appointment/entry/assignment/assignment-create-model";

@Component({
    standalone: true,
    imports: [ReactiveFormsModule, NgIf, MatHint, MatGridList, MatGridTile, MatFormField, MatInput, MatButton, AssignmentTabComponent, DateTimePickerComponent, EventTileContentComponent, RoomTabComponent, SelectionInput, MatDialogContent, MatDialogActions, GeneralCardComponent],
    templateUrl: './event-data-dialog.component.html',
    styleUrl: './event-data-dialog.component.scss'
})
export class EventDataDialogComponent {

    private readonly _title: string;
    private readonly _form: FormGroup;
    private readonly _rooms: RoomModel[] = [];

    public constructor(formBuilder: FormBuilder, roomService: RoomService, @Inject(MAT_DIALOG_DATA) data: {
        title: string,
        appointment: AppointmentEntryModel
    }, private readonly _appointmentService: AppointmentService, private readonly _courseService: CourseService, private readonly _matSnackBar: MatSnackBar,) {
        roomService.value$.subscribe((rooms: RoomModel[]): void => {
            this._rooms.length = 0;
            this._rooms.push(...rooms);
        });

        this._title = data.title;

        console.log(data.appointment)

        this._form = formBuilder.group({
            description: [null], room: [null], assignment: [null], publish: [null], submitUntil: [null]
        });

        this.appointment = data.appointment;
    }

    @Input() public set appointment(value: AppointmentEntryModel) {
        this._event = value;

        this.form.get('description')?.setValue(this.event.description);
        this.form.get('room')?.setValue(this.event.room);

        if (value.assignment) {
            const assignment: AssignmentModel = value.assignment;
            this.form.get('assignment')?.setValue(assignment.description);
            this.form.get('publish')?.setValue(assignment.publish);
            this.form.get('submitUntil')?.setValue(assignment.submitUntil);
            return;
        }

        // Default values when creating a new assignment
        this.form.get('publish')?.setValue(new Date());
        this.form.get('submitUntil')?.setValue(new Date(new Date().getTime() + (1000 * 60 * 60 * 24 * 7)));

        this._appointmentService.nextAppointments.subscribe((appointments: readonly AppointmentEntryModel[]): void =>
        {
            if(appointments.length === 0)
            {
                return;
            }
            this.form.get('submitUntil')?.setValue(appointments[0].start);
        })
    }

    protected get course(): CourseModel {
        return this._courseService.findCourseLazily(this.event.course) as CourseModel; // expect the course to exist
    }

    protected get title(): string {
        return this._title;
    }

    private _event!: AppointmentEntryModel;

    protected get event(): AppointmentEntryModel {
        return this._event;
    }

    protected get anyEdit(): boolean {
        return this.hasEdited('description') || this.hasEdited('room') || this.hasEdited('assignment')
    }

    protected get rooms(): RoomModel[] {
        return this._rooms;
    }

    protected get form(): FormGroup {
        return this._form;
    }

    private get assignmentCreateModel(): GenericAssignmentCreateModel {
        // if the 'assignment' field is undefined, these field below will be ignored
        return {
            description: this.form.get("assignment")?.value,
            publish: (this.form.get('publish')?.value as Date),
            submitUntil: (this.form.get('submitUntil')?.value as Date)
        }
    }

    protected hasEdited(field: 'description' | 'room' | 'assignment'): boolean {
        switch (field) {
            case 'description':
                return this.form.get('description')?.value !== this.event.description;
            case 'room':
                return this.form.get('room')?.value !== this.event.room;
            case 'assignment':
                const assignment: AssignmentModel | undefined = this.event.assignment;
                if (!assignment) {
                    return !!this.form.get('assignment')?.value;
                }

                const assignmentEdited: boolean = this.form.get('assignment')?.value !== assignment.description;
                const publishEdited: boolean = (this.form.get('publish')?.value as Date).getTime() !== assignment.publish.getTime();
                const submitUntilEdited: boolean = (this.form.get('submitUntil')?.value as Date).getTime() !== assignment.submitUntil.getTime();

                return assignmentEdited || publishEdited || submitUntilEdited;
        }
    }

    protected onSubmit(): void {
        if (!this.anyEdit) {
            return;
        }

        this._appointmentService.updateAppointment(this.event.id, AppointmentUpdateModel.fromObject({
            description: this.form.get('description')?.value,
            room: this.form.get('room')?.value, // undefined means not updating  !!!
            assignment: this.hasEdited('assignment') ? this.assignmentCreateModel : undefined
        })).subscribe((response: AppointmentEntryModel): void => {
            this.appointment = response;
            this._matSnackBar.open("The changes have been saved!", "", {duration: 2000});
        });
    }
}
