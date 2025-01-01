import {ChangeDetectionStrategy, Component, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {CourseModel} from "../../user/courses/models/course-model";
import {MatButton, MatIconButton} from "@angular/material/button";
import {CourseService} from "../../user/courses/course.service";
import {GeneralSelectionInput} from "../general-selection-input/general-selection-input.component";
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
    private _loading: boolean = false;

    protected get loading(): boolean {
        return this._loading;
    }

    private set loading(value: boolean) {
        this._loading = value;
    }

    constructor(private dialogReference: DialogRef, private _courseService: CourseService, formBuilder: FormBuilder) {
        this._courseService.courses$.subscribe((value: CourseModel[]): any => this._courses = value);
        this._form = formBuilder.group({
            course: [undefined, Validators.required],
            selected: [0, Validators.required],
        });
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
        const createModel: AppointmentCreateModel = AppointmentCreateModel.fromObject(this._standalone.form.value);
        this._courseService.createAppointment(courseId, createModel).subscribe({
            next: (): void => this.dialogReference.close(),
        });
    }

    private createFrequent(courseId: number): void
    {
        const createModel: FrequentAppointmentCreateModel = FrequentAppointmentCreateModel.fromObject(this._frequent.form.value);
        this._courseService.createFrequent(courseId, createModel).subscribe({
            next: (): void => this.dialogReference.close()
        });
    }

    protected canSubmit(): boolean {
        if(!this._standalone || !this._frequent || this.form.invalid)
        {
            return false;
        }

        switch (this.form.get('selected')?.value)
        {
            case 0:
                return this._standalone.form.valid;
            case 1:
                return this._frequent.form.valid;
        }
        return false;
    }

    protected get form(): FormGroup {
        return this._form;
    }
}
