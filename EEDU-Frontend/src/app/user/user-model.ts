import {ThemeModel} from "../theming/theme-model";
import {GroupModel} from "./group/group-model";

export enum UserStatus
{
    PRESENT = "PRESENT", EXCUSED = "EXCUSED", UNEXCUSED = "UNEXCUSED", PROSPECTIVE = "PROSPECTIVE"
}

export class UserModel
{
    constructor(public readonly id: number,
                public readonly firstName: string,
                public readonly lastName: string,
                public readonly loginName: string,
                public readonly status: UserStatus,
                public readonly groups: GroupModel[],
                public readonly theme: ThemeModel) { }

    public static fromObject(object: any): UserModel
    {
        const themeModel: ThemeModel = ThemeModel.fromObject(object.theme);
        const groupModel: GroupModel[] = object.groups.map((value: any): GroupModel => GroupModel.fromObject(value));
        return new UserModel(object.id, object.firstName, object.lastName, object.loginName, object.status, groupModel, themeModel);
    }

    public inGroup(name: string): boolean {
        return this.groups.map((value: GroupModel): string => value.name).includes(name);
    }

    public hasPrivilege(privilege: string): boolean {
        return this.groups && this.groups.some((group: GroupModel): boolean => group.hasPrivilege(privilege))
    }
}
