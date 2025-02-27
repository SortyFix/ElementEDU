import {Component, Type} from '@angular/core';
import {AbstractList} from "../../../common/abstract-list/abstract-list.component";
import {CourseModel} from "../course-model";
import {CourseService} from "../course.service";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateCourseComponent} from "../create-course/create-course.component";
import {NgIf} from "@angular/common";
import {MatProgressBar} from "@angular/material/progress-bar";
import {AbstractCourseComponentList} from "../abstract-course-components/list/abstract-course-component-list";
import {ListItemContent} from "../../../common/abstract-list/list-item-content";
import {CourseListItemComponent} from "./course-list-item/course-list-item.component";
import {DeleteCourseComponent} from "./delete-course/delete-course.component";

@Component({
    selector: 'app-course-list',
    imports: [
        MatProgressBar,
        AbstractList,
        MatIconButton,
        MatButton,
        MatIcon,
        NgIf,
    ],
    templateUrl: '../abstract-course-components/list/abstract-course-components-list.html',
    styleUrl: '../abstract-course-components/list/abstract-course-components-list.scss'
})
export class CourseListComponent extends AbstractCourseComponentList<bigint, CourseModel> {

    public constructor(service: CourseService, dialog: MatDialog) {
        super(service, dialog, CreateCourseComponent, DeleteCourseComponent, {
            title: (value: CourseModel): string => value.name,
            chips: (value: CourseModel): string[] => [
                value.subject.id,
                `${value.appointmentEntries.length} Appointment(s)`,
                `${value.frequentAppointments.length} Frequent Appointment(s)`
            ]
        });
    }

    protected override get content(): Type<ListItemContent<CourseModel>> | null {
        return CourseListItemComponent;
    }

    protected override get loaded(): boolean {
        return (super.service as CourseService).fetchedAdmin;
    }

    protected override subscribe(): void {
        const courseService: CourseService = super.service as CourseService;
        courseService.adminCourses$.subscribe((values: CourseModel[]): void => { this.values = values; });
    }
}
