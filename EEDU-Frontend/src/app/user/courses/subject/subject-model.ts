
export interface GenericSubject {
    id: number;
    name: string;
}

export class SubjectModel {
    constructor(
        private readonly _id: number,
        // This can be saved a number as it won't ever exceed 1.7976931348623157e+308. I am already overwhelmed with the
        // current amount of subjects in school. No need to create over 1.7976931348623157e+308
        private readonly _name: string
    ) {}

    public static fromObject(object: GenericSubject): SubjectModel {
        return new SubjectModel(object.id, object.name);
    }

    public get id(): number {
        return this._id;
    }

    public get name(): string {
        return this._name;
    }
}
