import { Component } from '@angular/core';
import {AbstractSimpleList} from "../abstract-simple-list";
import {ClassRoomModel} from "../../user/courses/classroom/class-room-model";
import {ClassRoomService} from "../../user/courses/classroom/class-room.service";
import {AbstractList} from "../../common/abstract-list/abstract-list.component";
import {ManagementLoadingBar} from "../management-loading-bar/management-loading-bar.component";

@Component({
  selector: 'app-class-room-list',
    imports: [
        AbstractList,
        ManagementLoadingBar
    ],
  templateUrl: './file-contents.html',
})
export class ClassRoomListComponent extends AbstractSimpleList<ClassRoomModel> {
    public constructor(service: ClassRoomService) { super(service); }
    protected override title(value: ClassRoomModel): string { return value.name; }
    protected override icon(value: ClassRoomModel): string { return 'groups'; }
}
