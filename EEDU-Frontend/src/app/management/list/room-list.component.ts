import { Component } from '@angular/core';
import {AbstractSimpleList} from "../abstract-simple-list";
import {RoomModel} from "../../user/courses/room/room-model";
import {RoomService} from "../../user/courses/room/room.service";
import {AbstractList} from "../../common/abstract-list/abstract-list.component";
import {ManagementLoadingBar} from "../management-loading-bar/management-loading-bar.component";

@Component({
  selector: 'app-room-list',
    imports: [
        AbstractList,
        ManagementLoadingBar
    ],
  templateUrl: './file-contents.html',
})
export class RoomListComponent extends AbstractSimpleList<RoomModel>{
    public constructor(service: RoomService) { super(service); }
    protected override title(value: RoomModel): string { return value.name; }
    protected override icon(value: RoomModel): string { return 'meeting_room'; }
}
