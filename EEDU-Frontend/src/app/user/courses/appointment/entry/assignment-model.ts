export interface GenericAssignment {
    description: string;
    publish: number;
    submitUntil: number;
}

export class AssignmentModel {

    constructor(
        private _description: string,
        private _publish: number,
        private _submitUntil: number
    ) {}

    public get description(): string {
        return this._description;
    }

    public get publish(): Date {
        return new Date(this._publish);
    }

    public get submitUntil(): Date {
        return new Date(this._submitUntil);
    }

    public static fromObject(obj: GenericAssignment): AssignmentModel {
        return new AssignmentModel(obj.description, obj.publish, obj.submitUntil);
    }
}
