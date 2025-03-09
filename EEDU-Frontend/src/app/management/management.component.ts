import {Component, OnInit} from '@angular/core';
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
import {
    IllnessNotificationModel
} from "../illness-notification/model/illness-notification-model";
import {FileService} from "../file/file.service";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {ManagementService} from "./management.service";
import {IllnessNotificationStatus} from "../illness-notification/illness-notification-status";
import {MatDialog} from "@angular/material/dialog";
import {ManagementCourseSectionComponent} from "./management-course-section/management-course-section.component";
import {ManagementUserSectionComponent} from "./management-user-section/management-user-section.component";


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
        MatIcon,
        NgForOf,
        ManagementCourseSectionComponent,
        ManagementUserSectionComponent
    ],
    templateUrl: './management.component.html',
    styleUrl: './management.component.scss'
})
export class ManagementComponent implements OnInit {

    userList: UserModel[] = [];

    illnessNotifications: IllnessNotificationModel[] = []

    public constructor(
        private readonly _matDialog: MatDialog,
        protected managementService: ManagementService,
        protected userService: UserService,
        protected fileService: FileService)
    {}

    ngOnInit(): void {
        this.managementService.getPendingNotifications().subscribe((list: IllnessNotificationModel[]): void => {
            this.illnessNotifications = list;
            this.userService.fetchAll.subscribe((users: UserModel[]): void => { this.userList = users });
        });
    }

    public downloadFile(id: bigint)
    {
        this.fileService.downloadFile(id);
    }

    public respondToNotification(id: bigint, status: IllnessNotificationStatus): void
    {
        this.managementService.respondToNotification(id, status).subscribe((): void => {
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

