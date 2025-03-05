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
import {NgComponentOutlet, NgForOf, NgIf} from "@angular/common";
import {MatTab, MatTabContent, MatTabGroup, MatTabLabel} from "@angular/material/tabs";
import {icons} from "../../environment/styles";
import {
    IllnessNotificationModel
} from "../illness-notification/model/illness-notification-model";
import {SubjectListComponent} from "../user/courses/subject/subject-dialogs/subject-dialogs.component";
import {RoomListComponent} from "../user/courses/room/room-dialogs/room-list.component";
import {ClassRoomListComponent} from "../user/courses/classroom/class-room-dialogs/class-room-dialogs.component";
import {CourseListComponent} from "../user/courses/course-dialogs/course-dialogs.component";
import {FileService} from "../file/file.service";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {ManagementService} from "./management.service";
import {IllnessNotificationStatus} from "../illness-notification/illness-notification-status";

export interface CourseTab
{
    label: string,
    icon: string,
    component: Type<any>
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
        NgComponentOutlet,
    ],
    templateUrl: './management.component.html',
    styleUrl: './management.component.scss'
})
export class ManagementComponent implements OnInit {

    private readonly _courseComponentsTabs: CourseTab[] = [
        { label: 'Courses', icon: icons.course, component: CourseListComponent },
        { label: 'Class Rooms', icon: icons.classroom, component: ClassRoomListComponent },
        { label: 'Rooms', icon: icons.room, component: RoomListComponent },
        { label: 'Subjects', icon: icons.subject, component: SubjectListComponent }
    ];

    userList: UserModel[] = [];

    illnessNotifications: IllnessNotificationModel[] = []

    public constructor(protected managementService: ManagementService, protected userService: UserService, protected fileService: FileService) {
    }

    ngOnInit(): void {
        this.managementService.getPendingNotifications().subscribe((list: IllnessNotificationModel[]): void => {
            this.illnessNotifications = list;
            this.userService.fetchAll.subscribe((users: UserModel[]): void => { this.userList = users });
        });
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
}

