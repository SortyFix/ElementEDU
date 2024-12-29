import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {CourseModel} from "../../user/courses/models/course-model";
import {MatButton, MatIconButton} from "@angular/material/button";
import {CourseService} from "../../user/courses/course.service";
import {DateTimePickerComponent} from "../date-time-picker/date-time-picker.component";
import {GeneralSelectionInput} from "../general-selection-input/general-selection-input.component";
import {MatDialogClose, MatDialogRef} from "@angular/material/dialog";
import {AppointmentCreateModel} from "../../user/courses/models/appointments/appointment-create-model";
import {MatStep, MatStepLabel, MatStepper, MatStepperNext, MatStepperPrevious} from "@angular/material/stepper";
import {MatIcon} from "@angular/material/icon";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatSuffix} from "@angular/material/form-field";
import {NgIf} from "@angular/common";
import {DurationPickerComponent} from "../duration-picker/duration-picker.component";

@Component({
  selector: 'app-create-appointment',
  standalone: true,
    imports: [
        MatCard,
        MatCardHeader,
        MatCardContent,
        FormsModule,
        ReactiveFormsModule,
        MatButton,
        MatCardTitle,
        DateTimePickerComponent,
        GeneralSelectionInput,
        MatDialogClose,
        MatStepper,
        MatStep,
        MatStepperNext,
        MatIconButton,
        MatIcon,
        MatStepperPrevious,
        MatStepLabel,
        MatSlideToggle,
        MatSuffix,
        NgIf,
        DurationPickerComponent
    ],
  templateUrl: './create-appointment.component.html',
  styleUrl: './create-appointment.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateAppointmentComponent  {

    private readonly _currentDate: Date = new Date();
    private _courses!: CourseModel[];
    private readonly _firstForm: FormGroup;
    private readonly _form: FormGroup;

    protected get currentDate(): Date {
        return this._currentDate;
    }


    protected get firstForm(): FormGroup {
        return this._firstForm;
    }

    protected courseToString(): (course: CourseModel) => string
    {
        return (course: CourseModel): string => course.name;
    }

    constructor(private _dialogRef: MatDialogRef<CreateAppointmentComponent>, private courseService: CourseService, formBuilder: FormBuilder) {
        this.courseService.courses$.subscribe((value: CourseModel[]): any => this._courses = value);
        this._firstForm = formBuilder.group({
            course: [undefined, Validators.required],
            scheduled: [false, Validators.required]
        });

        this._form = formBuilder.group({
            course: [undefined, Validators.required],
            start: [new Date(), Validators.required],
            until: [new Date(new Date().getTime() + (1000 * 60 * 45)), Validators.required],
        }, { validators: this.timeValidator });
    }

    protected get courses(): CourseModel[] {
        return this._courses;
    }

    private timeValidator(group: FormGroup): {untilBeforeStart: boolean} | null {
        const start: Date = group.get('start')?.value;
        const until: Date = group.get('until')?.value;

        return start && until && until > start ? null : { untilBeforeStart: true };
    }

    protected get form(): FormGroup {
        return this._form;
    }

    protected onSubmit(): void {
        if (this._form.valid) {
            const formValue: { course: string, start: Date, until: Date } = this._form.value;

            const course: CourseModel | undefined = this.courses.find((course: CourseModel): boolean => course.name === formValue.course);
            if(!course)
            {
                console.log("course invalid!"); //TODO REMOVE LATER
                return;
            }
            const appointment: AppointmentCreateModel = new AppointmentCreateModel(formValue.start, formValue.until);
            this.courseService.createAppointment(course.id, appointment).subscribe({next: (): void => this._dialogRef.close()})

        }
    }

    protected readonly CourseModel = CourseModel;
}
