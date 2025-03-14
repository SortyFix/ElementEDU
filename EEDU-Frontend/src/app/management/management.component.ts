import {Component, OnInit, Type} from '@angular/core';
import { UserModel } from '../user/user-model';
import {UserService} from "../user/user.service";
import {
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelDescription,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle
} from '@angular/material/expansion';
import {NgForOf, NgIf} from "@angular/common";
import {MatTab, MatTabContent, MatTabGroup, MatTabLabel} from "@angular/material/tabs";
import {icons} from "../../environment/styles";
import {
    IllnessNotificationModel
} from "../illness-notification/model/illness-notification-model";
import {FileService} from "../file/file.service";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {ManagementService} from "./management.service";
import {IllnessNotificationStatus} from "../illness-notification/illness-notification-status";
import {ListItemInfo} from "../common/abstract-list/abstract-list.component";
import {CourseModel} from "../user/courses/course-model";
import {ClassRoomModel} from "../user/courses/classroom/class-room-model";
import {EntityListComponent} from "../entity/entity-list/entity-list.component";
import {SubjectService} from "../user/courses/subject/subject.service";
import {RoomService} from "../user/courses/room/room.service";
import {ClassRoomService} from "../user/courses/classroom/class-room.service";
import {CourseService} from "../user/courses/course.service";
import {EntityService} from "../entity/entity-service";
import {ListItemContent} from "../common/abstract-list/list-item-content";
import {CourseListItemComponent} from "../user/courses/course-dialogs/course-list-item/course-list-item.component";
import {ComponentType} from "@angular/cdk/overlay";
import {CreateCourseComponent} from "../user/courses/course-dialogs/course-dialogs.component";
import {DeleteDialogComponent} from "../common/delete-dialog/delete-dialog.component";
import {
    CreateSubjectComponent,
    DeleteSubjectComponent
} from "../user/courses/subject/subject-dialogs/subject-dialogs.component";
import {
    CreateClassRoomComponent,
    DeleteClassRoomComponent
} from "../user/courses/classroom/class-room-dialogs/class-room-dialogs.component";
import {CreateRoomComponent, DeleteRoomComponent} from "../user/courses/room/room-dialogs/room-list.component";
import {MatDialog} from "@angular/material/dialog";

export interface CourseTab
{
    label: string;
    icon: string;
    service: EntityService<any, any, any, any>;
    itemInfo: ListItemInfo<any>;
    newDialog: ComponentType<any>,
    deleteDialog: ComponentType<any>,
    content?: Type<ListItemContent<any>>
}


@Component({
    selector: 'app-management',
    imports: [
        MatAccordion,
        MatExpansionPanel,
        MatExpansionPanelHeader,
        MatExpansionPanelTitle,
        MatExpansionPanelDescription,
        NgForOf,
        MatIcon,
        MatButton,
        NgIf,
        MatTab,
        MatTabGroup,
        MatTabContent,
        MatTabLabel,
        MatIcon,
        NgForOf,
        EntityListComponent
    ],
    templateUrl: './management.component.html',
    styleUrl: './management.component.scss'
})
export class ManagementComponent implements OnInit {

    private readonly _courseComponentsTabs: CourseTab[];

    userList: UserModel[] = [];

    illnessNotifications: IllnessNotificationModel[] = []

    public constructor(
        private readonly _matDialog: MatDialog,
        protected managementService: ManagementService,
        protected userService: UserService,
        protected fileService: FileService,
        courseService: CourseService,
        classRoomService: ClassRoomService,
        roomService: RoomService,
        subjectService: SubjectService)
    {
        const idTitle: (obj: { id: string }) => string =  (obj: { id: string }): string => obj.id

        this._courseComponentsTabs = [{
            label: 'Courses',
            icon: icons.course,
            service: courseService,
            newDialog: CreateCourseComponent,
            deleteDialog: DeleteDialogComponent,
            itemInfo: {
                title: (value: CourseModel): string => value.name,
                chips: (value: CourseModel): string[] => [
                    `${value.teacher?.name}`,
                    `${value.students?.length} Student(s)`, value.subject.id,
                    `${value.appointmentEntries.length} Appointment(s)`,
                    `${value.frequentAppointments.length} Frequent Appointment(s)`
                ]
            },
            content: CourseListItemComponent
        }, {
            label: 'Class Rooms',
            icon: icons.classroom,
            service: classRoomService,
            newDialog: CreateClassRoomComponent,
            deleteDialog: DeleteClassRoomComponent,
            itemInfo: {
                title: idTitle,
                chips: (value: ClassRoomModel): string[] => [`Tutor: ${value.tutor.name}`, `${value.students.length} Users`]
            }
        }, {
            label: 'Rooms',
            icon: icons.room,
            service: roomService,
            newDialog: CreateRoomComponent,
            deleteDialog: DeleteRoomComponent,
            itemInfo: { title: idTitle }
        }, {
            label: 'Subjects',
            icon: icons.subject,
            service: subjectService,
            newDialog: CreateSubjectComponent,
            deleteDialog: DeleteSubjectComponent,
            itemInfo: { title: idTitle }
        }];
    }

    ngOnInit(): void {
        this.managementService.getPendingNotifications().subscribe((list: IllnessNotificationModel[]): void => {
            this.illnessNotifications = list;
            this.userService.fetchAll.subscribe((users: UserModel[]): void => { this.userList = users });
        });
    }

    protected openDialog(dialog: ComponentType<any>)
    {
        return this._matDialog.open(dialog, {width: '600px', disableClose: true});
    }

    protected get courseComponentTabs(): CourseTab[] {
        return this._courseComponentsTabs;
    }

    public downloadFile(id: bigint)
    {
        this.fileService.downloadFile(id);
    }

    public respondToNotification(id: bigint, status: IllnessNotificationStatus): void
    {
        this.managementService.respondToNotification(id, status).subscribe((accepted: boolean): void => {
            let acceptedNoteIndex: number = this.illnessNotifications.findIndex((element: IllnessNotificationModel): boolean => element.id == id);
            if(acceptedNoteIndex >= 0)
            {
                this.illnessNotifications.splice(acceptedNoteIndex, 1);
            }
        });
    }

    protected readonly IllnessNotificationStatus = IllnessNotificationStatus;
    protected readonly open = open;
}

