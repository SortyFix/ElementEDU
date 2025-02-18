import { Component } from '@angular/core';
import {AbstractList} from "../../../common/abstract-list/abstract-list.component";
import {ManagementLoadingBar} from "../../../management/management-loading-bar/management-loading-bar.component";
import {CourseModel} from "../course-model";
import {CourseService} from "../course.service";
import {MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateCourseComponent} from "../create-course/create-course.component";
import {AbstractCourseComponentList} from "../abstract-course-components/abstract-course-component-list";

@Component({
  selector: 'app-course-list',
    imports: [
        AbstractList,
        ManagementLoadingBar,
        MatIconButton,
        MatIcon,
    ],
  templateUrl: '../abstract-course-components/abstract-course-components-list.html',
})
export class CourseListComponent extends AbstractCourseComponentList<CourseModel> {

    public constructor(service: CourseService, dialog: MatDialog) { super(service, dialog, CreateCourseComponent); }

    protected override subscribe(): void {
        (super.service as CourseService).adminCourses$.subscribe(
            (values: CourseModel[]): void => { this.values = values; }
        );
    }

    protected override get loaded(): boolean { return (super.service as CourseService).fetchedAdmin; }

    protected override title(course: CourseModel): string { return course.name; }

    protected override icon(course: CourseModel): string { return 'library_books'; }

    protected override chips(course: CourseModel): string[] {
        return [
            course.subject.name,
            `${course.appointmentEntries.length} Appointments and ${course.frequentAppointments.length} Frequent Appointments`
        ];
    }
}
