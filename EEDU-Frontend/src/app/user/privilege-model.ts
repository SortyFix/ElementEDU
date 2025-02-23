export class PrivilegeModel {
    constructor(private readonly _id: string) { }

    public get id(): string {
        return this._id;
    }

    public static fromObject(object: { id: string }): PrivilegeModel {
        return new PrivilegeModel(object.id);
    }
}
