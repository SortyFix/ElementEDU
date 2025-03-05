import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {GenericGroup, GroupModel} from "./group-model";
import {EntityService} from "../../entity/entity-service";

@Injectable({
    providedIn: 'root'
})
export class GroupService extends EntityService<string, GroupModel, GenericGroup, { id: string, privileges: string[] }>
{
    public constructor(http: HttpClient) {
        super(http, "user/groups");
    }

    public override translate(obj: any): GroupModel {
        return GroupModel.fromObject(obj);
    }
}
