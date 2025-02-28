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
import {MatButton} from "@angular/material/button";
import {MatTab, MatTabContent, MatTabGroup, MatTabLabel} from "@angular/material/tabs";
import {MatIcon} from "@angular/material/icon";
import {icons} from "../../environment/styles";
import {
    GenericIllnessNotificationModel,
    IllnessNotificationModel
} from "../illness-notification/model/illness-notification-model";
import {SubjectListComponent} from "../user/courses/subject/subject-dialogs/subject-list.component";
import {RoomListComponent} from "../user/courses/room/room-dialogs/room-list.component";
import {ClassRoomListComponent} from "../user/courses/classroom/class-room-dialogs/class-room-list.component";
import {CourseListComponent} from "../user/courses/course-dialogs/course-list.component";
import {FileService} from "../file/file.service";
import {HttpClient} from "@angular/common/http";

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

    PREFIX: string = "http://localhost:8080/api/v1";

    public constructor(protected userService: UserService, protected fileService: FileService, private http: HttpClient) {}

    public ngOnInit(): void {
        this.getPendingNotifications();
        this.userService.fetchAll.subscribe((users: UserModel[]): void => { this.userList = users });
    }

    protected get courseComponentTabs(): CourseTab[] {
        return this._courseComponentsTabs;
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

