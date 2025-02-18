import { Component } from '@angular/core';
import {AbstractList} from "../../../../common/abstract-list/abstract-list.component";
import {ClassRoomModel} from "../class-room-model";
import {ClassRoomService} from "../class-room.service";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateClassRoomComponent} from "../create-class-room/create-class-room.component";
import {MatButton, MatIconButton} from "@angular/material/button";
import {AbstractCourseComponentList} from "../../abstract-course-components/abstract-course-component-list";
import {NgIf} from "@angular/common";
import {MatProgressBar} from "@angular/material/progress-bar";

@Component({
    selector: 'app-class-room-list',
    imports: [
        MatProgressBar,
        AbstractList,
        MatIconButton,
        MatButton,
        MatIcon,
        NgIf,
    ],
    templateUrl: '../../abstract-course-components/abstract-course-components-list.html',
    styleUrl: '../../abstract-course-components/abstract-course-components-list.scss'
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
