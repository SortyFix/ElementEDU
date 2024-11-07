import {PrivilegeModel} from "./privilege-model";

export class GroupModel
{
    constructor(public readonly id: bigint,
                public readonly name: string,
                public readonly privileges: PrivilegeModel[]) { }

    public hasPrivilege(privilege: string): boolean {
        return this.privileges.some((privilegeModel: PrivilegeModel): boolean => privilegeModel.name === privilege);
    }
}
