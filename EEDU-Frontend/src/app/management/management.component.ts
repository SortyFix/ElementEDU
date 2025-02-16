import {Component, OnInit} from '@angular/core';
import { UserModel } from '../user/user-model';
import {UserService} from "../user/user.service";
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {CourseListComponent} from "../user/courses/course-list/course-list.component";
import {ClassRoomListComponent} from "../user/courses/classroom/class-room-list/class-room-list.component";
import {RoomListComponent} from "../user/courses/room/room-list/room-list.component";
import {SubjectListComponent} from "../user/courses/subject/subject-list/subject-list.component";
import {Observable, Observer} from "rxjs";
import {AsyncPipe, NgComponentOutlet} from "@angular/common";

export interface CourseComponent { label: string, component: any}

@Component({
    selector: 'app-management',
    imports: [
        MatTab,
        MatTabGroup,
        AsyncPipe,
        NgComponentOutlet,
    ],
    templateUrl: './management.component.html',
    standalone: true,
    styleUrl: './management.component.scss'
})
export class ManagementComponent implements OnInit {

    userList: UserModel[] = [];
    private readonly _asyncCourseComponents: Observable<CourseComponent[]>;

    constructor(protected userService: UserService) {
        this._asyncCourseComponents = new Observable((observer: Observer<CourseComponent[]>): void => {
            setTimeout((): void => {
                observer.next([
                    { label: 'Courses', component: CourseListComponent },
                    { label: 'Classrooms', component: ClassRoomListComponent },
                    { label: 'Rooms', component: RoomListComponent },
                    { label: 'Subjects', component: SubjectListComponent },
                ]);
            }, 1000);
        });
    }


    protected get asyncCourseComponents(): Observable<CourseComponent[]> {
        return this._asyncCourseComponents;
    }

    ngOnInit(): void {
        this.userService.fetchAll.subscribe((users: UserModel[]): void => { this.userList = users });
    }
}
