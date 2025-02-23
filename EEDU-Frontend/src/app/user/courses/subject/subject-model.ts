export interface GenericSubject {id: string;}

export class SubjectModel {

    constructor(private readonly _id: string) {}

    public static fromObject(object: GenericSubject): SubjectModel {
        return new SubjectModel(object.id);
    }

    public get id(): string {
        return this._id;
    }
}
