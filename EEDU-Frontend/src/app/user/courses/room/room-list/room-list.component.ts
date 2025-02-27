import {Component} from '@angular/core';
import {RoomModel} from "../room-model";
import {RoomService} from "../room.service";
import {AbstractList} from "../../../../common/abstract-list/abstract-list.component";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateRoomComponent} from "../create-room/create-room.component";
import {MatButton, MatIconButton} from "@angular/material/button";
import {NgIf} from "@angular/common";
import {MatProgressBar} from "@angular/material/progress-bar";
import {AbstractCourseComponentList} from "../../abstract-course-components/list/abstract-course-component-list";
import {DeleteRoomComponent} from "./delete-room/delete-room.component";

@Component({
    selector: 'app-room-list',
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
export class RoomListComponent extends AbstractCourseComponentList<string, RoomModel> {

    public constructor(service: RoomService, dialog: MatDialog) {
        super(service, dialog, CreateRoomComponent, DeleteRoomComponent, {title: (value: RoomModel): string => value.id});
    }
}
