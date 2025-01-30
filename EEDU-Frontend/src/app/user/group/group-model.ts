import {GenericPrivilegeModel, PrivilegeModel} from "./privilege-model";

export interface GenericGroupModel
{
    id: bigint;
    name: string;
    privileges: GenericPrivilegeModel[];
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
        const models: PrivilegeModel[] = obj.privileges.map((privilege: any): PrivilegeModel =>
        {
            return PrivilegeModel.fromObject(privilege);
        });

        return new GroupModel(BigInt(obj.id), obj.name, models);
    }

    public hasPrivilege(privilege: string): boolean {
        return this.privileges.some((privilegeModel: PrivilegeModel): boolean => privilegeModel.name === privilege);
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
}
