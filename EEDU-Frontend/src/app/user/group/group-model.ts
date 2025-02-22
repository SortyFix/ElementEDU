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
        return new GroupModel(obj.id, obj.privileges.map((privilege: GenericPrivilegeModel): PrivilegeModel =>
            PrivilegeModel.fromObject(privilege)
        ));
    }

    public hasPrivilege(privilege: string): boolean {
        return this.privileges.some((privilegeModel: PrivilegeModel): boolean => privilegeModel.id === privilege);
    }

    public get id(): string {
        return this._id;
    }

    public get privileges(): PrivilegeModel[] {
        return this._privileges;
    }
}
