export class SubjectModel {
    constructor(
        private readonly _id: number,
        private readonly _name: string
    ) {}

    public static fromObject(object: any): SubjectModel {
        return new SubjectModel(object.id, object.name);
    }

    public get id(): number {
        return this._id;
    }

    public get name(): string {
        return this._name;
    }
}
