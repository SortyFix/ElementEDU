import { Component } from '@angular/core';
import {AbstractCourseComponentList} from "../../abstract-course-component-list";
import {RoomModel} from "../room-model";
import {RoomService} from "../room.service";
import {ManagementLoadingBar} from "../../../../management/management-loading-bar/management-loading-bar.component";
import {AbstractList} from "../../../../common/abstract-list/abstract-list.component";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateRoomComponent} from "../create-room/create-room.component";

@Component({
  selector: 'app-room-list',
    imports: [
        AbstractList,
        ManagementLoadingBar,
        MatIcon
    ],
  templateUrl: '../../abstract-course-components-list.html',
})
export class RoomListComponent extends AbstractCourseComponentList<RoomModel>{
    public constructor(service: RoomService, dialog: MatDialog) { super(service, dialog, CreateRoomComponent); }
    protected override title(value: RoomModel): string { return value.name; }
    protected override icon(value: RoomModel): string { return 'meeting_room'; }
}
