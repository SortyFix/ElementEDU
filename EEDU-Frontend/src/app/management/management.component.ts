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

    PREFIX: string = "http://localhost:8080/api/v1";

    constructor(protected userService: UserService, protected fileService: FileService, private http: HttpClient) {
    }

    ngOnInit(): void {
        this.getPendingNotifications();
        this.userService.fetchAll.subscribe((users: UserModel[]): void => { this.userList = users });
    }

    private getPendingNotifications(): void
    {
        this.http.get<GenericIllnessNotificationModel[]>(`${this.PREFIX}/illness/management/get-pending`, {
            withCredentials: true
        }).subscribe((list: GenericIllnessNotificationModel[]): void => {
             list.forEach((obj: GenericIllnessNotificationModel): void => {
                let model: IllnessNotificationModel = IllnessNotificationModel.fromObject(obj);
                this.illnessNotifications.push(model);
             })
            console.log(this.illnessNotifications);
        })
    }

    public downloadFile(id: bigint)
    {
        this.fileService.downloadFile(id);
    }
}

