import { Component } from '@angular/core';
import {AbstractList} from "../../../../common/abstract-list/abstract-list.component";
import {ManagementLoadingBar} from "../../../../management/management-loading-bar/management-loading-bar.component";
import {ClassRoomModel} from "../class-room-model";
import {ClassRoomService} from "../class-room.service";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateClassRoomComponent} from "../create-class-room/create-class-room.component";
import {MatIconButton} from "@angular/material/button";
import {AbstractCourseComponentList} from "../../abstract-course-components/abstract-course-component-list";

@Component({
  selector: 'app-class-room-list',
    imports: [
        AbstractList,
        ManagementLoadingBar,
        MatIconButton,
        MatIcon
    ],
  templateUrl: '../../abstract-course-components/abstract-course-components-list.html',
})
export class ClassRoomListComponent extends AbstractCourseComponentList<ClassRoomModel> {
    public constructor(service: ClassRoomService, dialog: MatDialog) { super(service, dialog, CreateClassRoomComponent); }
    protected override title(value: ClassRoomModel): string { return value.name; }
    protected override icon(value: ClassRoomModel): string { return 'groups'; }

    protected override chips(value: ClassRoomModel): string[] {

        return [
            `Tutor: ${value.tutor.name}`,
            `${value.students.length} Users`,
            `${value.courses.length} Courses`
        ];
    }
}
