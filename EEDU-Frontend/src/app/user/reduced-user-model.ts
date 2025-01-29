export interface GenericReducedUserModel {
    id: bigint;
    firstName: string;
    lastName: string;
}

export class ReducedUserModel {

    constructor(
        private readonly _id: bigint,
        private readonly _firstName: string,
        private readonly _lastName: string
    ) {}

    public static fromObject(obj: GenericReducedUserModel): ReducedUserModel
    {
        return new ReducedUserModel(obj.id, obj.firstName, obj.lastName);
    }

    public get id(): bigint {
        return this._id;
    }

    public get name(): string {
        return `${this.lastName}, ${this.firstName}`;
    }

    public get firstName(): string {
        return this._firstName;
    }

    public get lastName(): string {
        return this._lastName;
    }
}
