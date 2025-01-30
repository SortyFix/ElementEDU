export interface GenericPrivilegeModel
{
    id: bigint;
    name: string;
}

export class PrivilegeModel
{
    constructor(
        private readonly _id: bigint,
        private readonly _name: string
    ) { }

    public static fromObject(obj: GenericPrivilegeModel): PrivilegeModel
    {
        return new PrivilegeModel(BigInt(obj.id), obj.name);
    }

    public get id(): bigint {
        return this._id;
    }

    public get name(): string {
        return this._name;
    }
}
