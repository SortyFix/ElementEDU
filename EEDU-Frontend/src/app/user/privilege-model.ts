export class PrivilegeModel
{
    constructor(public readonly id: bigint,
                public readonly name: string) { }

    public static fromObject(object: any): PrivilegeModel
    {
        return new PrivilegeModel(object.id, object.name);
    }

}
