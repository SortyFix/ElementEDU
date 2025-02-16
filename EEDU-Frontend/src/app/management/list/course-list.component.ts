import { Component } from '@angular/core';
import {AbstractList} from "../../common/abstract-list/abstract-list.component";
import {CourseModel} from "../../user/courses/course-model";
import {CourseService} from "../../user/courses/course.service";
import {ManagementLoadingBar} from "../management-loading-bar/management-loading-bar.component";
import {AbstractSimpleList} from "../abstract-simple-list";

@Component({
  selector: 'app-course-list',
    imports: [
        AbstractList,
        ManagementLoadingBar,
    ],
  templateUrl: './file-contents.html',
})
export class CourseListComponent extends AbstractSimpleList<CourseModel> {

    public constructor(service: CourseService) { super(service); }

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
