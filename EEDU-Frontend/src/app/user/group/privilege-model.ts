export interface GenericPrivilege
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

    public static fromObject(obj: GenericPrivilege): PrivilegeModel
    {
        return new PrivilegeModel(obj.id, obj.name);
    }

    public get id(): bigint {
        return this._id;
    }

    public get name(): string {
        return this._name;
    }
}
