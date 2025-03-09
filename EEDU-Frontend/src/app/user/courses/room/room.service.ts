import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {GenericRoom, RoomModel} from "./room-model";
import {EntityService} from "../../../entity/entity-service";
import {CreateRoomDialogComponent} from "./create-room-dialog/create-room-dialog.component";

@Injectable({
    providedIn: 'root'
})
export class RoomService extends EntityService<string, RoomModel, GenericRoom, GenericRoom> {

    public constructor(http: HttpClient) {
        super(http, 'course/room', {
            createPrivilege: "ROOM_CREATE",
            deletePrivilege: "ROOM_DELETE",
            fetchPrivilege: "ROOM_GET"
        }, CreateRoomDialogComponent);
    }

    public override translate(obj: GenericRoom): RoomModel {
        return RoomModel.fromObject(obj);
    }
}
