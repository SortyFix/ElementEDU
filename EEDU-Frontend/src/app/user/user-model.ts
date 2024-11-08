import {ThemeEntity} from "../theming/theme-entity";
import {GroupModel} from "./group-model";
import {PrivilegeModel} from "./privilege-model";

export enum UserStatus
{
    PRESENT = "PRESENT", EXCUSED = "EXCUSED", UNEXCUSED = "UNEXCUSED", PROSPECTIVE = "PROSPECTIVE"
}

export class UserModel
{
    constructor(public readonly id: bigint,
                public readonly firstName: string,
                public readonly lastName: string,
                public readonly loginName: string,
                public readonly status: UserStatus,
                public readonly groups: GroupModel[],
                public readonly theme: ThemeEntity) { }

    public inGroup(name: string): boolean {
        return this.groups.map((value: GroupModel): string => value.name).includes(name);
    }

    public hasPrivilege(privilege: string): boolean {
        return this.groups && this.groups.some((group: GroupModel): boolean => group.hasPrivilege(privilege))
    }
}
