import {ThemeEntity} from "../theming/theme-entity";

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
                public readonly theme: ThemeEntity) { }
}
