export interface GenericPrivilegeModel {
    id: string;
}

export class PrivilegeModel {
    constructor(private readonly _id: string) { }

    public get id(): string {
        return this._id;
    }

    public static fromObject(obj: GenericPrivilegeModel): PrivilegeModel {
        return new PrivilegeModel(obj.id);
    }
}
