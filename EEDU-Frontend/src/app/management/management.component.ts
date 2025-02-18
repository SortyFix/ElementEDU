import {Component, OnInit, Type} from '@angular/core';
import { UserModel } from '../user/user-model';
import {UserService} from "../user/user.service";
import {MatTab, MatTabContent, MatTabGroup, MatTabLabel} from "@angular/material/tabs";
import {CourseListComponent} from "../user/courses/course-list/course-list.component";
import {ClassRoomListComponent} from "../user/courses/classroom/class-room-list/class-room-list.component";
import {RoomListComponent} from "../user/courses/room/room-list/room-list.component";
import {SubjectListComponent} from "../user/courses/subject/subject-list/subject-list.component";
import {MatIcon} from "@angular/material/icon";
import {NgComponentOutlet, NgForOf} from "@angular/common";

export interface CourseTab
{
    label: string,
    icon: string,
    component: Type<any>
}

@Component({
    selector: 'app-management',
    imports: [
        MatTab,
        MatTabGroup,
        MatTabContent,
        MatTabLabel,
        MatIcon,
        NgForOf,
        NgComponentOutlet,
    ],
    templateUrl: './management.component.html',
    standalone: true,
    styleUrl: './management.component.scss'
})
export class ManagementComponent implements OnInit {

    private readonly _courseComponentsTabs: CourseTab[] = [
        { label: 'Courses', icon: 'book_5', component: CourseListComponent },
        { label: 'Class Rooms', icon: 'groups', component: ClassRoomListComponent },
        { label: 'Rooms', icon: 'meeting_room', component: RoomListComponent },
        { label: 'Subjects', icon: 'subject', component: SubjectListComponent }
    ];


    protected get courseComponentTabs(): CourseTab[] {
        return this._courseComponentsTabs;
    }

    userList: UserModel[] = [];
    constructor(protected userService: UserService) {}

    ngOnInit(): void {
        this.userService.fetchAll.subscribe((users: UserModel[]): void => { this.userList = users });
    }
}
