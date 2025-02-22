export interface GenericPrivilegeModel
{
    id: string;
}

export class PrivilegeModel
{
    constructor(private readonly _id: string) { }

    public static fromObject(obj: GenericPrivilegeModel): PrivilegeModel
    {
        return new PrivilegeModel(obj.id);
    }

    public get id(): string {
        return this._id;
    }
}
