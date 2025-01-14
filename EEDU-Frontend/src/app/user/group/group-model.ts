import {GenericPrivilege, PrivilegeModel} from "./privilege-model";

export interface GenericGroupModel
{
    id: bigint;
    name: string;
    privileges: GenericPrivilege[];
}

export class GroupModel
{
    constructor(
        private readonly _id: bigint,
        private readonly _name: string,
        private readonly _privileges: PrivilegeModel[]
    ) { }

    public static fromObject(obj: GenericGroupModel): GroupModel
    {
        return new GroupModel(BigInt(obj.id), obj.name, obj.privileges.map((privilege: any): PrivilegeModel =>
        {
            return PrivilegeModel.fromObject(privilege);
        }));
    }

    public get id(): bigint {
        return this._id;
    }

    public get name(): string {
        return this._name;
    }

    public get privileges(): PrivilegeModel[] {
        return this._privileges;
    }

    public hasPrivilege(privilege: string): boolean {
        return this.privileges.some((privilegeModel: PrivilegeModel): boolean => privilegeModel.name === privilege);
    }
}
