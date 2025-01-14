import {ChangeDetectionStrategy, Component, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatCardActions, MatCardContent} from "@angular/material/card";
import {MatButton} from "@angular/material/button";
import {MatDialogClose} from "@angular/material/dialog";
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {
    CreateStandaloneAppointmentComponent
} from "./create-standalone-appointment/create-standalone-appointment.component";
import {
    CreateFrequentAppointmentComponent
} from "./create-frequent-appointment/create-frequent-appointment.component";
import {DialogRef} from "@angular/cdk/dialog";
import {GeneralCreateComponent} from "../../../../timetable/general-create-component/general-create.component";
import {CourseModel} from "../../course-model";
import {RoomModel} from "../../room/room-model";
import {RoomService} from "../../room/room.service";
import {AppointmentService} from "../appointment.service";
import {CourseService} from "../../course.service";
import {AppointmentCreateModel, GenericAppointmentCreateModel} from "../entry/appointment-create-model";
import {
    FrequentAppointmentCreateModel,
    GenericFrequentAppointmentCreateModel
} from "../frequent/frequent-appointment-create-model";
import {GeneralSelectionInput} from "../../../../timetable/general-selection-input/general-selection-input.component";

/**
 * A component for creating appointments
 *
 * This component provides a user interface for creating either standalone or frequent appointments.
 * Users can select the type of appointment, configure its details, and submit the data for processing.
 *
 * The component uses {@link ChangeDetectionStrategy.OnPush} for optimized change detection.
 *
 * @author Ivo Quiring
 */
@Component({
  selector: 'app-create-appointment',
  standalone: true,
    imports: [
        MatCardContent,
        FormsModule,
        ReactiveFormsModule,
        GeneralSelectionInput,
        MatDialogClose,
        MatTabGroup,
        MatTab,
        MatCardActions,
        MatButton,
        CreateStandaloneAppointmentComponent,
        CreateFrequentAppointmentComponent,
        GeneralCreateComponent,
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

    /**
     * Initializes the component and its dependencies
     *
     * This constructor sets up the dependencies required for the component and initializes
     * the form controls. It subscribes to the {@link CourseService} to fetch available courses
     * and updates the _courses property accordingly. It also invokes the {@link RoomService}
     * to fetch available rooms and updates the _rooms property. Note that this component's loading state
     * is set to false, when the rooms have successfully been fetched
     *
     * @param dialogReference a reference to the dialog managing the component's lifecycle and interactions.
     * @param _roomService the {@link RoomService} instance used to fetch and manage room-related data.
     * @param _appointmentService the {@link AppointmentService} instance used for appointment-related operations.
     * @param courseService the {@link CourseService} instance used to retrieve available courses.
     * @param formBuilder the {@link FormBuilder} instance used to construct the reactive form for the component.
     * @public
     */
    public constructor(
        private dialogReference: DialogRef,
        private readonly _roomService: RoomService,
        private readonly _appointmentService: AppointmentService,
        courseService: CourseService,
        formBuilder: FormBuilder
    ) {

        //assume they've been fetched before
        courseService.courses$.subscribe((value: CourseModel[]): any => this._courses = value);
        this._form = formBuilder.group({
            course: [undefined, Validators.required],
            selected: [0, Validators.required],
        });

        this._roomService.fetchRooms().subscribe((value: RoomModel[]): void => {
            this._rooms = value;
            this.loading = false;
        })
    }

    /**
     * Handles the submission of the form
     *
     * This method validates the form and, if it is valid, starts the creation of either
     * a standalone or frequent appointment based on the user's selection. The component enters
     * a loading state during the submission process.
     *
     * - If the selected form value is 0, it creates a standalone appointment using {@link createStandalone}.
     * - If the selected form value is 1, it creates a frequent appointment using {@link createFrequent}.
     *
     * @protected
     */
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

    /**
     * Determines whether the form can be submitted
     *
     * This method checks the validity of the form and additional conditions to ensure
     * that the submission can proceed:
     * - The _standalone and _frequent objects must be defined.
     * - The overall form must be valid.
     * - Depending on the selected type (standalone or frequent), the corresponding sub-form
     *   must also be valid.
     *
     * @returns true if the form can be submitted, otherwise, false.
     * @protected
     */
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

    /**
     * Retrieves the RoomService instance used by the component
     *
     * This accessor provides the instance of {@link RoomService} used for managing room operations.
     *
     * @returns the {@link RoomService} instance associated with the component.
     * @protected
     */
    protected get roomService(): RoomService {
        return this._roomService;
    }

    /**
     * Retrieves the AppointmentService instance used by the component
     *
     * This accessor provides the instance of {@link AppointmentService} used for managing appointment operations.
     *
     * @returns the {@link AppointmentService} instance associated with the component.
     * @protected
     */
    protected get appointmentService(): AppointmentService {
        return this._appointmentService;
    }

    /**
     * Retrieves the list of available rooms managed by the component
     *
     * This accessor provides an array of {@link RoomModel} objects representing the rooms
     * that can be used within the component's context.
     *
     * @returns an array of {@link RoomModel} instances.
     * @protected
     */
    protected get rooms(): RoomModel[] {
        return this._rooms;
    }

    /**
     * Retrieves the list of available courses managed by the component
     *
     * This accessor provides an array of {@link CourseModel} objects representing the courses
     * that can be used within the component's context.
     *
     * @returns an array of {@link CourseModel} instances.
     * @protected
     */
    protected get courses(): CourseModel[] {
        return this._courses;
    }

    /**
     * Retrieves the form group managed by the component
     *
     * This accessor provides the {@link FormGroup} instance that contains the form controls
     * and their current state for the component.
     *
     * @returns the {@link FormGroup} instance associated with the component.
     * @protected
     */
    protected get form(): FormGroup {
        return this._form;
    }

    /**
     * Retrieves the current loading state of the component
     *
     * This accessor provides a boolean indicating whether the component is currently in a loading state.
     *
     * @returns true if the component is loading, otherwise, false.
     * @protected
     */
    protected get loading(): boolean {
        return this._loading;
    }

    /**
     * Updates the loading state of the component
     *
     * This setter allows the loading state of the component to be updated.
     * The loading state is used to indicate whether the component is processing data.
     *
     * @param value true to indicate the component is loading; otherwise, false.
     * @private
     */
    private set loading(value: boolean) {
        this._loading = value;
    }

    /**
     * Creates a standalone appointment for a specific course
     *
     * This method handles the creation of a standalone appointment by transforming the
     * form data into the required format and sending it to the backend service. It ensures
     * the room property is converted from a {@link RoomModel} to its corresponding id before
     * making the API call.
     *
     * Once the appointment is successfully created, the dialog is closed.
     *
     * @param courseId the id of the course for which the standalone appointment is being created.
     * @private
     */
    private createStandalone(courseId: bigint): void
    {
        // I must first transform the room model into the room id
        const object: {
            start: Date,
            until: Date,
            room: RoomModel | number,
            duration: number,
        } = this._standalone.form.value;
        object.room = (object.room as RoomModel)?.id;

        this.appointmentService.createAppointment(courseId,
            [AppointmentCreateModel.fromObject(object as GenericAppointmentCreateModel)]
        ).subscribe({ next: (): void => this.dialogReference.close() });
    }

    /**
     * Creates a frequent appointment for a specific course
     *
     * This method handles the creation of a frequent appointment by transforming the
     * form data into the required format and sending it to the backend service. It ensures
     * the room property is converted from a {@link RoomModel} to its corresponding id before
     * making the API call.
     *
     * The frequency property is included to indicate how often the appointment recurs.
     * Once the appointment is successfully created, the dialog is closed.
     *
     * @param courseId the id of the course for which the frequent appointment is being created.
     * @private
     */
    private createFrequent(courseId: bigint): void
    {
        // I must first transform the room model into the room id
        const object: {
            start: Date,
            until: Date,
            room: RoomModel | number,
            duration: number,
            frequency: number
        } = this._frequent.form.value;
        object.room = Number((object.room as RoomModel).id);

        this.appointmentService.createFrequent(courseId,
            [FrequentAppointmentCreateModel.fromObject((object as GenericFrequentAppointmentCreateModel))]
        ).subscribe({ next: (): void => this.dialogReference.close() });
    }
}
