import {PrivilegeModel} from "./privilege-model";
import {model} from "@angular/core";

export class GroupModel
{
    constructor(public readonly id: bigint,
                public readonly name: string,
                public readonly privileges: PrivilegeModel[]) { }

    public static fromObject(obj: any): GroupModel
    {
        const models: PrivilegeModel[] = obj.privileges.map((privilege: any): PrivilegeModel =>
        {
            return PrivilegeModel.fromObject(privilege);
        });

        return new GroupModel(BigInt(obj.id), obj.name, models);
    }

    public hasPrivilege(privilege: string): boolean {
        return this.privileges.some((privilegeModel: PrivilegeModel): boolean => privilegeModel.name === privilege);
    }
}
