export class AssignmentModel {

    constructor(private _description: string, private _submitUntil: number) {}

    public static fromObject(obj: any): AssignmentModel
    {
        return new AssignmentModel(obj.description, obj.submitUntil);
    }

    public get description(): string {
        return this._description;
    }

    public get submitUntil(): number {
        return this._submitUntil;
    }
}
