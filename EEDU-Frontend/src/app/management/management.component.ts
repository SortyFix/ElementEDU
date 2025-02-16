import {Component, OnInit} from '@angular/core';
import { UserModel } from '../user/user-model';
import {UserService} from "../user/user.service";
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {RoomListComponent} from "./list/room-list.component";
import {SubjectListComponent} from "./list/subject-list.component";
import {ClassRoomListComponent} from "./list/class-room-list.component";
import {CourseListComponent} from "./list/course-list.component";

@Component({
    selector: 'app-management',
    imports: [
        MatTab,
        MatTabGroup,
        RoomListComponent,
        SubjectListComponent,
        ClassRoomListComponent,
        CourseListComponent,
    ],
    templateUrl: './management.component.html',
    standalone: true,
    styleUrl: './management.component.scss'
})
export class ManagementComponent implements OnInit {

    userList: UserModel[] = [];

    constructor(protected userService: UserService) {
    }

    ngOnInit(): void {
        this.userService.fetchAll.subscribe((users: UserModel[]): void => { this.userList = users });
    }
}
