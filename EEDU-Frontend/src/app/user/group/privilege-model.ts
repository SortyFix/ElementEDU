export class PrivilegeModel
{
    constructor(public readonly id: bigint,
                public readonly name: string) { }

    public static fromObject(obj: any): PrivilegeModel
    {
        return new PrivilegeModel(BigInt(obj.id), obj.name);
    }
}
