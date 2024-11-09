import {PrivilegeModel} from "./privilege-model";

export class GroupModel
{
    constructor(public readonly id: bigint,
                public readonly name: string,
                public readonly privileges: PrivilegeModel[]) { }

    public static fromObject(object: any): GroupModel
    {
        const privilegeModel: PrivilegeModel[] = object.privileges.map((value: any): PrivilegeModel =>
            PrivilegeModel.fromObject(value)
        );
        return new GroupModel(object.id, object.name, privilegeModel);
    }

    public hasPrivilege(privilege: string): boolean {
        return this.privileges.some((privilegeModel: PrivilegeModel): boolean => privilegeModel.name === privilege);
    }
}
