import { Component } from '@angular/core';
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
export class CourseListComponent extends AbstractCourseComponentList<CourseModel> {

    public constructor(service: CourseService, dialog: MatDialog)
    {
        super(service, dialog, CreateCourseComponent, {
            title: (value: CourseModel): string => value.name,
            chips: (value: CourseModel): string[] => [
                value.subject.name,
                `${value.appointmentEntries.length} Appointments`,
                `${value.frequentAppointments.length} Frequent Appointments`
            ]
        });
    }

    protected override get loaded(): boolean
    {
        return (super.service as CourseService).fetchedAdmin;
    }

    protected override subscribe(): void {
        const courseService: CourseService = super.service as CourseService;
        courseService.adminCourses$.subscribe((values: CourseModel[]): void => { this.values = values; });
    }
}
