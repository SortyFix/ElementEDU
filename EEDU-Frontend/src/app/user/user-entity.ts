export enum UserStatus
{
    PRESENT = "PRESENT", EXCUSED = "EXCUSED", UNEXCUSED = "UNEXCUSED", PROSPECTIVE = "PROSPECTIVE"
}

export class UserEntity
{

    public readonly id: bigint;
    public readonly firstName: string;
    public readonly lastName: string;
    public readonly loginName: string;
    public readonly status: UserStatus;

    constructor(id: bigint, firstName: string, lastName: string, loginName: string, status: UserStatus)
    {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.loginName = loginName;
        this.status = status;
    }
}
