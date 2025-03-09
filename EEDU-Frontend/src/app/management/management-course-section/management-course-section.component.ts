import {Component} from '@angular/core';
import {CourseService} from "../../user/courses/course.service";
import {
    LazyLoadedAccordionComponent,
    LazyLoadedAccordionTab
} from "../lazy-loaded-accordion/lazy-loaded-accordion.component";
import {ClassRoomService} from "../../user/courses/classroom/class-room.service";
import {RoomService} from "../../user/courses/room/room.service";
import {SubjectService} from "../../user/courses/subject/subject.service";
import {DeleteDialogComponent} from "../../common/delete-dialog/delete-dialog.component";
import {icons} from "../../../environment/styles";
import {DeleteSubjectComponent} from "../../user/courses/subject/subject-dialogs/subject-dialogs.component";
import {DeleteRoomComponent} from "../../user/courses/room/room-dialogs/room-list.component";
import {ClassRoomModel} from "../../user/courses/classroom/class-room-model";
import {DeleteClassRoomComponent} from "../../user/courses/classroom/class-room-dialogs/class-room-dialogs.component";
import {CourseModel} from "../../user/courses/course-model";
import {MatCard, MatCardContent, MatCardHeader, MatCardSubtitle, MatCardTitle} from "@angular/material/card";
import {CourseListItemComponent} from "../../user/courses/course-dialogs/course-list-item/course-list-item.component";

@Component({
    selector: 'management-course-section',
    imports: [LazyLoadedAccordionComponent, MatCard, MatCardTitle, MatCardSubtitle, MatCardHeader, MatCardContent],
    templateUrl: './management-course-section.component.html',
    styleUrl: './management-course-section.component.scss'
})
export class ManagementCourseSectionComponent {

    private readonly _tabs: LazyLoadedAccordionTab[];

    public constructor(courseService: CourseService, classRoomService: ClassRoomService, roomService: RoomService, subjectService: SubjectService) {
        const idTitle: (obj: { id: string }) => string = (obj: { id: string }): string => obj.id

        this._tabs = [{
            label: 'Courses',
            icon: icons.course,
            service: courseService,
            deleteDialog: DeleteDialogComponent,
            itemInfo: {
                title: (value: CourseModel): string => value.name,
                chips: (value: CourseModel): string[] => [`${value.teacher?.name}`, `${value.students?.length} Student(s)`, value.subject.id, `${value.appointmentEntries.length} Appointment(s)`, `${value.frequentAppointments.length} Frequent Appointment(s)`],
                content: CourseListItemComponent
            }
        }, {
            label: 'Class Rooms',
            icon: icons.classroom,
            service: classRoomService,
            deleteDialog: DeleteClassRoomComponent,
            itemInfo: {
                title: idTitle,
                chips: (value: ClassRoomModel): string[] => [`Tutor: ${value.tutor.name}`, `${value.students.length} Users`]
            }
        }, {
            label: 'Rooms',
            icon: icons.room,
            service: roomService,
            deleteDialog: DeleteRoomComponent,
            itemInfo: {title: idTitle}
        }, {
            label: 'Subjects',
            icon: icons.subject,
            service: subjectService,
            deleteDialog: DeleteSubjectComponent,
            itemInfo: {title: idTitle}
        }];
    }

    public get tabs(): LazyLoadedAccordionTab[] {
        return this._tabs;
    }
}
