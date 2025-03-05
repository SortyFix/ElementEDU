import {AccountType} from "./account-type";

export interface GenericUserCreateModel {
    firstName: string;
    lastName: string;
    loginName: string;
    accountType: AccountType;
    enabled: boolean;
    theme: { id: bigint };
    groups: { id: string }[];
}

export interface UserCreatePacket
{
    firstName: string;
    lastName: string;
    loginName: string;
    accountType: string;
    enabled: boolean;
    theme: number;
    groups: string[];
}

export class UserCreateModel
{
    public constructor(
        private readonly _firstName: string,
        private readonly _lastName: string,
        private readonly _loginName: string,
        private readonly _accountType: AccountType,
        private readonly _enabled: boolean,
        private readonly _theme: bigint,
        private readonly _groups: string[]
    ) {}

    public static fromObject(obj: GenericUserCreateModel): UserCreateModel {
        return new UserCreateModel(
            obj.firstName,
            obj.lastName,
            obj.loginName,
            obj.accountType,
            obj.enabled,
            obj.theme.id,
            obj.groups.map((item: { id: string }): string => item.id)
        );
    }

    public get toPacket(): UserCreatePacket
    {
        return {
            firstName: this.firstName,
            lastName: this.lastName,
            loginName: this.loginName,
            accountType: this.accountType.toUpperCase(),
            enabled: this.enabled,
            theme: Number(this.theme),
            groups: this.groups
        }
    }

    public get firstName(): string {
        return this._firstName;
    }

    public get lastName(): string {
        return this._lastName;
    }

    public get loginName(): string {
        return this._loginName;
    }

    public get accountType(): AccountType {
        return this._accountType;
    }

    public get enabled(): boolean {
        return this._enabled;
    }

    public get theme(): bigint {
        return this._theme;
    }

    public get groups(): string[] {
        return this._groups;
    }
}
