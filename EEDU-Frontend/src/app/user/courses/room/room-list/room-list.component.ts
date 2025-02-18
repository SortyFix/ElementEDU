import { Component } from '@angular/core';
import {RoomModel} from "../room-model";
import {RoomService} from "../room.service";
import {ManagementLoadingBar} from "../../../../management/management-loading-bar/management-loading-bar.component";
import {AbstractList} from "../../../../common/abstract-list/abstract-list.component";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateRoomComponent} from "../create-room/create-room.component";
import {MatIconButton} from "@angular/material/button";
import {AbstractCourseComponentList} from "../../abstract-course-components/abstract-course-component-list";

@Component({
  selector: 'app-room-list',
    imports: [
        AbstractList,
        ManagementLoadingBar,
        MatIconButton,
        MatIcon
    ],
  templateUrl: '../../abstract-course-components/abstract-course-components-list.html',
})
export class RoomListComponent extends AbstractCourseComponentList<RoomModel>{
    public constructor(service: RoomService, dialog: MatDialog) { super(service, dialog, CreateRoomComponent); }
    protected override title(value: RoomModel): string { return value.name; }
    protected override icon(value: RoomModel): string { return 'meeting_room'; }
}
