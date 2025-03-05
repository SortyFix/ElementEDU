import {GenericPrivilege, PrivilegeModel} from "./privilege-model";

export interface GenericGroup {
    id: string;
    privileges: GenericPrivilege[];
}

export class GroupModel {

    constructor(
        private readonly _id: string,
        private readonly _privileges: PrivilegeModel[]
    ) { }

    public get id(): string {
        return this._id;
    }

    public get privileges(): PrivilegeModel[] {
        return this._privileges;
    }

    public static fromObject(obj: GenericGroup): GroupModel {
        return new GroupModel(obj.id, obj.privileges.map((privilege: GenericPrivilege): PrivilegeModel =>
            PrivilegeModel.fromObject(privilege)
        ));
    }

    public hasPrivilege(privilege: string): boolean {
        return this.privileges.some((privilegeModel: PrivilegeModel): boolean => privilegeModel.id === privilege);
    }
}
