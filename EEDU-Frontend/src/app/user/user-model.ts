import {ThemeModel} from "../theming/theme-model";
import {GenericGroup, GroupModel} from "./group/group-model";
import {AccountType} from "./account-type";
import {ClassRoomModel, GenericClassRoom} from "./courses/classroom/class-room-model";

export enum UserStatus {PRESENT = "PRESENT", EXCUSED = "EXCUSED", UNEXCUSED = "UNEXCUSED", PROSPECTIVE = "PROSPECTIVE" }

export interface GenericUser {
    id: bigint;
    firstName: string;
    lastName: string;
    loginName: string;
    accountType: string;
    status: string
    groups: GenericGroup[],
    theme: any,
    classroom?: GenericClassRoom
}

export class UserModel {
    constructor(
        private readonly _id: bigint,
        private readonly _firstName: string,
        private readonly _lastName: string,
        private readonly _loginName: string,
        private readonly _accountType: AccountType,
        private readonly _status: UserStatus,
        private readonly _groups: GroupModel[],
        private readonly _theme: ThemeModel,
        private readonly _classroom?: ClassRoomModel
    ) {}

    public get id(): bigint {
        return this._id;
    }

    public get firstName(): string {
        return this._firstName;
    }

    public get lastName(): string {
        return this._lastName;
    }

    public get name(): string {
        return `${this.lastName}, ${this.firstName}`;
    }

    public get loginName(): string {
        return this._loginName;
    }

    public get accountType(): AccountType {
        return this._accountType;
    }

    public get status(): UserStatus {
        return this._status;
    }

    public get groups(): GroupModel[] {
        return this._groups;
    }

    public get theme(): ThemeModel {
        return this._theme;
    }

    public get classroom(): ClassRoomModel | undefined {
        return this._classroom;
    }

    public static fromObject(object: GenericUser): UserModel {
        const themeModel: ThemeModel = ThemeModel.fromObject(object.theme);
        return new UserModel(
            object.id,
            object.firstName,
            object.lastName,
            object.loginName,
            AccountType[object.accountType as keyof typeof AccountType],
            UserStatus[object.status as keyof typeof UserStatus],
            object.groups.map((value: any): GroupModel => GroupModel.fromObject(value)),
            themeModel,
            !!object.classroom ? ClassRoomModel.fromObject(object.classroom) : undefined,
        );
    }

    public inGroup(name: string): boolean {
        return this.groups.map((value: GroupModel): string => value.id).includes(name);
    }

    public hasPrivilege(privilege: string): boolean {
        return this.groups && this.groups.some((group: GroupModel): boolean => group.hasPrivilege(privilege))
    }
}
