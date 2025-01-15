export interface GenericAssignment
{
    description: string;
    submitUntil: number;
}

export class AssignmentModel {

    constructor(private _description: string, private _submitUntil: number) {}

    public static fromObject(obj: GenericAssignment): AssignmentModel
    {
        return new AssignmentModel(obj.description, obj.submitUntil);
    }

    public get description(): string {
        return this._description;
    }

    public get submitUntil(): Date {
        return new Date(this._submitUntil);
    }
}
