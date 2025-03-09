import {Injectable} from '@angular/core';
import {GenericPrivilege, PrivilegeModel} from "./privilege-model";
import {HttpClient} from "@angular/common/http";
import {EntityService} from "../../../entity/entity-service";
import {CreatePrivilegesDialogComponent} from "./create-privilege-dialog/create-privileges-dialog.component";

@Injectable({
    providedIn: 'root'
})
export class PrivilegeService extends EntityService<string, PrivilegeModel, GenericPrivilege, GenericPrivilege> {

    public constructor(http: HttpClient) {
        super(http, 'user/group/privilege', {
            createPrivilege: "PRIVILEGE_CREATE",
            deletePrivilege: "PRIVILEGE_DELETE",
            fetchPrivilege: "PRIVILEGE_GET"
        }, CreatePrivilegesDialogComponent);
    }

    public override translate(obj: any): PrivilegeModel {
        return PrivilegeModel.fromObject(obj);
    }
}
