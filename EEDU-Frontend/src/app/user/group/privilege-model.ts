export interface GenericPrivilege {
    id: string;
}

export class PrivilegeModel {

    public constructor(private readonly _id: string) { }

    public get id(): string {
        return this._id;
    }

    public static fromObject(obj: GenericPrivilege): PrivilegeModel {
        return new PrivilegeModel(obj.id);
    }
}
