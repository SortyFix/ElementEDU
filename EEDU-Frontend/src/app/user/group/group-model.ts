import {GenericPrivilegeModel, PrivilegeModel} from "./privilege-model";

export interface GenericGroupModel
{
    id: string;
    privileges: GenericPrivilegeModel[];
}

export class GroupModel
{
    constructor(
        private readonly _id: string,
        private readonly _privileges: PrivilegeModel[]
    ) { }

    public static fromObject(obj: GenericGroupModel): GroupModel
    {
        const models: PrivilegeModel[] = obj.privileges.map((privilege: any): PrivilegeModel =>
        {
            return PrivilegeModel.fromObject(privilege);
        });

        return new GroupModel(obj.id, models);
    }

    public hasPrivilege(privilege: string): boolean {
        return this.privileges.some((privilegeModel: PrivilegeModel): boolean => privilegeModel.name === privilege);
    }

    public get id(): string {
        return this._id;
    }

    public get privileges(): PrivilegeModel[] {
        return this._privileges;
    }
}
