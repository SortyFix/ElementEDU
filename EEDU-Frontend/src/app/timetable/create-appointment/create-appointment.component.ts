import {ChangeDetectionStrategy, Component, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {CourseModel} from "../../user/courses/models/course-model";
import {MatButton, MatIconButton} from "@angular/material/button";
import {CourseService} from "../../user/courses/course.service";
import {MatDialogClose} from "@angular/material/dialog";
import {MatIcon} from "@angular/material/icon";
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {
    CreateStandaloneAppointmentComponent
} from "./create-standalone-appointment/create-standalone-appointment.component";
import {
    CreateFrequentAppointmentComponent
} from "./create-scheduled-appointment/create-frequent-appointment.component";
import {AppointmentCreateModel} from "../../user/courses/models/appointments/appointment-create-model";
import {DialogRef} from "@angular/cdk/dialog";
import {MatProgressBar} from "@angular/material/progress-bar";
import {NgIf} from "@angular/common";
import {FrequentAppointmentCreateModel} from "../../user/courses/models/appointments/frequent-appointment-create-model";
import {RoomModel} from "../../user/courses/room/room-model";
import {GeneralSelectionInput} from "./general-selection-input/general-selection-input.component";
import {RoomService} from "../../user/courses/room/room.service";

@Component({
  selector: 'app-create-appointment',
  standalone: true,
    imports: [
        MatCard,
        MatCardHeader,
        MatCardContent,
        FormsModule,
        ReactiveFormsModule,
        MatCardTitle,
        GeneralSelectionInput,
        MatDialogClose,
        MatIconButton,
        MatIcon,
        MatTabGroup,
        MatTab,
        MatCardActions,
        MatButton,
        CreateStandaloneAppointmentComponent,
        CreateFrequentAppointmentComponent,
        MatProgressBar,
        NgIf
    ],
  templateUrl: './create-appointment.component.html',
  styleUrl: './create-appointment.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateAppointmentComponent {

    @ViewChild('standalone') private _standalone!: CreateStandaloneAppointmentComponent;
    @ViewChild('frequent') private _frequent!: CreateFrequentAppointmentComponent;
    private readonly _form: FormGroup;
    private _courses!: CourseModel[];
    private _loading: boolean = true;
    private _rooms: RoomModel[] = [];

    protected get loading(): boolean {
        return this._loading;
    }

    private set loading(value: boolean) {
        this._loading = value;
    }

    constructor(private dialogReference: DialogRef, private readonly _roomService: RoomService, private readonly _courseService: CourseService, formBuilder: FormBuilder) {
        this._courseService.courses$.subscribe((value: CourseModel[]): any => this._courses = value);
        this._form = formBuilder.group({
            course: [undefined, Validators.required],
            selected: [0, Validators.required],
        });

        this._roomService.fetchRooms().subscribe((value: RoomModel[]): void => {
            this._rooms = value;
            this.loading = false;
        })
    }


    protected get rooms(): RoomModel[] {
        return this._rooms;
    }

    protected get courses(): CourseModel[] {
        return this._courses;
    }

    protected onSubmit(): void {
        if(this.canSubmit())
        {
            this.loading = true;
            const course: CourseModel = this.form.get('course')!.value;
            switch (this.form.get('selected')?.value)
            {
                case 0:
                    this.createStandalone(course.id);
                    return

                case 1:
                    this.createFrequent(course.id);
                    return;
            }
        }
    }

    private createStandalone(courseId: number): void
    {
        // I must first transform the room model into the room id
        const object: {
            start: Date,
            until: Date,
            room: RoomModel | number,
            duration: number,
        } = this._frequent.form.value;
        object.room = (object.room as RoomModel)?.id;

        this._courseService.createAppointment(courseId, [AppointmentCreateModel.fromObject(object as {
            start: Date,
            until: Date,
            room: number,
            duration: number,
        })]).subscribe({ next: (): void => this.dialogReference.close() });
    }

    private createFrequent(courseId: number): void
    {
        // I must first transform the room model into the room id
        const object: {
            start: Date,
            until: Date,
            room: RoomModel | number,
            duration: number,
            frequency: number
        } = this._frequent.form.value;
        object.room = (object.room as RoomModel).id;

        this._courseService.createFrequent(courseId, [FrequentAppointmentCreateModel.fromObject((object as {
            start: Date,
            until: Date,
            room: number,
            duration: number,
            frequency: number
        }))]).subscribe({ next: (): void => this.dialogReference.close() });
    }

    protected canSubmit(): boolean {
        if(!this._standalone || !this._frequent || this.form.invalid)
        {
            return false;
        }

        switch (this.form.get('selected')?.value)
        {
            case 0: return this._standalone.form.valid;
            case 1: return this._frequent.form.valid;
            default: return false;
        }
    }

    protected get form(): FormGroup {
        return this._form;
    }
}
