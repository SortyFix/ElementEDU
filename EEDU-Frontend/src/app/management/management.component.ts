import {Component, OnInit} from '@angular/core';
import {UserListComponent} from "../user/user-list/user-list.component";
import { UserModel } from '../user/user-model';
import {UserService} from "../user/user.service";
import {
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelDescription, MatExpansionPanelHeader,
    MatExpansionPanelTitle
} from "@angular/material/expansion";
import {HttpClient} from "@angular/common/http";
import {
    GenericIllnessNotificationModel,
    IllnessNotificationModel
} from "../illness-notification/model/illness-notification-model";
import {NgForOf, NgIf} from "@angular/common";
import {FileService} from "../file/file.service";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {ManagementService} from "./management.service";
import {IllnessNotificationStatus} from "../illness-notification/illness-notification-status";
import {Observable} from "rxjs";

@Component({
    selector: 'app-management',
    imports: [
        UserListComponent,
        MatAccordion,
        MatExpansionPanel,
        MatExpansionPanelHeader,
        MatExpansionPanelHeader,
        MatExpansionPanelTitle,
        MatExpansionPanelDescription,
        NgForOf,
        MatIcon,
        MatButton,
        NgIf
    ],
    templateUrl: './management.component.html',
    standalone: true,
    styleUrl: './management.component.scss'
})
export class ManagementComponent implements OnInit {

    userList: UserModel[] = [];

    illnessNotifications: IllnessNotificationModel[] = []

    constructor(protected managementService: ManagementService, protected userService: UserService, protected fileService: FileService, private http: HttpClient) {
    }

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

