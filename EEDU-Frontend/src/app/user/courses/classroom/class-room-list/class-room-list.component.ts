import {Component} from '@angular/core';
import {AbstractList} from "../../../../common/abstract-list/abstract-list.component";
import {ClassRoomModel} from "../class-room-model";
import {ClassRoomService} from "../class-room.service";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateClassRoomComponent} from "../create-class-room/create-class-room.component";
import {MatButton, MatIconButton} from "@angular/material/button";
import {NgIf} from "@angular/common";
import {MatProgressBar} from "@angular/material/progress-bar";
import {AbstractCourseComponentList} from "../../abstract-course-components/list/abstract-course-component-list";

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
    templateUrl: '../../abstract-course-components/list/abstract-course-components-list.html',
    styleUrl: '../../abstract-course-components/list/abstract-course-components-list.scss'
})
export class ClassRoomListComponent extends AbstractCourseComponentList<bigint, ClassRoomModel> {

    public constructor(service: ClassRoomService, dialog: MatDialog) {
        super(service, dialog, CreateClassRoomComponent, {
            title: (value: ClassRoomModel): string => value.name,
            chips: (value: ClassRoomModel): string[] => [
                `Tutor: ${value.tutor.name}`,
                `${value.students.length} Users`
            ]
        });
    }
}
