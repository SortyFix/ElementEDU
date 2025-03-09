import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {GenericGroup, GroupModel} from "./group-model";
import {EntityService} from "../../entity/entity-service";
import {CreateGroupDialogComponent} from "./create-group-dialog/create-group-dialog.component";
import {AbstractSimpleCreateEntity} from "../../entity/create-entity/abstract-simple-create-entity";

@Injectable({
    providedIn: 'root'
})
export class GroupService extends EntityService<string, GroupModel, GenericGroup, { id: string, privileges: string[] }>
{
    public constructor(http: HttpClient) {
        super(http, "user/group", CreateGroupDialogComponent as unknown as typeof AbstractSimpleCreateEntity);
    }

    public override translate(obj: any): GroupModel {
        return GroupModel.fromObject(obj);
    }
}
