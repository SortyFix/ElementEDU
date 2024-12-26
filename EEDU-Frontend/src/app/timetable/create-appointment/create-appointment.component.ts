import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {CourseModel} from "../../user/courses/models/course-model";
import {MatButton} from "@angular/material/button";
import {CourseService} from "../../user/courses/course.service";
import {DateTimePickerComponent} from "../date-time-picker/date-time-picker.component";
import {CourseSelectorComponent} from "../course-selector/course-selector.component";
import {MatDialogClose} from "@angular/material/dialog";

@Component({
  selector: 'app-create-appointment',
  standalone: true,
    imports: [
        MatCard,
        MatCardHeader,
        MatCardContent,
        FormsModule,
        ReactiveFormsModule,
        MatCardActions,
        MatButton,
        MatCardTitle,
        DateTimePickerComponent,
        CourseSelectorComponent,
        MatDialogClose
    ],
  templateUrl: './create-appointment.component.html',
  styleUrl: './create-appointment.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateAppointmentComponent  {

    private readonly _courses: CourseModel[];
    private readonly _form: FormGroup;

    constructor(courseService: CourseService, formBuilder: FormBuilder) {
        this._courses = courseService.courses;
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
            const formValue = this._form.value;
            console.log('Form Submitted', formValue);
        }
    }
}
