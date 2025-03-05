import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {GenericRoom, RoomModel} from "./room-model";
import {EntityService} from "../../../entity/entity-service";

@Injectable({
    providedIn: 'root'
})
export class RoomService extends EntityService<string, RoomModel, GenericRoom, GenericRoom> {

    public constructor(http: HttpClient) { super(http, 'course/room'); }

    public override translate(obj: GenericRoom): RoomModel {
        return RoomModel.fromObject(obj);
    }
}
