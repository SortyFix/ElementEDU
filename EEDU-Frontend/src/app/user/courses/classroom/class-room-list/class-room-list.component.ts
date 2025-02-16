import { Component } from '@angular/core';
import {AbstractList} from "../../../../common/abstract-list/abstract-list.component";
import {ManagementLoadingBar} from "../../../../management/management-loading-bar/management-loading-bar.component";
import {AbstractCourseComponentList} from "../../abstract-course-component-list";
import {ClassRoomModel} from "../class-room-model";
import {ClassRoomService} from "../class-room.service";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateClassRoomComponent} from "../create-class-room/create-class-room.component";

@Component({
  selector: 'app-class-room-list',
    imports: [
        AbstractList,
        ManagementLoadingBar,
        MatIcon
    ],
  templateUrl: '../../abstract-course-components-list.html',
})
export class ClassRoomListComponent extends AbstractCourseComponentList<ClassRoomModel> {
    public constructor(service: ClassRoomService, dialog: MatDialog) { super(service, dialog, CreateClassRoomComponent); }
    protected override title(value: ClassRoomModel): string { return value.name; }
    protected override icon(value: ClassRoomModel): string { return 'groups'; }

    protected override chips(value: ClassRoomModel): string[] {

        return [`${value.users.length} Users`, `${value.courses.length} Courses`];
    }
}
