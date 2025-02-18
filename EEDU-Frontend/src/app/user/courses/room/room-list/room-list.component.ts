import { Component } from '@angular/core';
import {RoomModel} from "../room-model";
import {RoomService} from "../room.service";
import {ManagementLoadingBar} from "../../../../management/management-loading-bar/management-loading-bar.component";
import {AbstractList} from "../../../../common/abstract-list/abstract-list.component";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateRoomComponent} from "../create-room/create-room.component";
import {MatButton, MatIconButton} from "@angular/material/button";
import {AbstractCourseComponentList} from "../../abstract-course-components/abstract-course-component-list";
import {NgIf} from "@angular/common";

@Component({
    selector: 'app-room-list',
    imports: [
        AbstractList,
        ManagementLoadingBar,
        MatIconButton,
        MatButton,
        MatIcon,
        NgIf
    ],
    templateUrl: '../../abstract-course-components/abstract-course-components-list.html',
    styleUrl: '../../abstract-course-components/abstract-course-components-list.scss'
})
export class RoomListComponent extends AbstractCourseComponentList<RoomModel>{
    public constructor(service: RoomService, dialog: MatDialog) { super(service, dialog, CreateRoomComponent); }
    protected override title(value: RoomModel): string { return value.name; }
    protected override icon(value: RoomModel): string { return 'meeting_room'; }
}
