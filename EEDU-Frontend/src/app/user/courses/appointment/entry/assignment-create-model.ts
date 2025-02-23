export interface GenericAssignmentCreateModel {
    description: string;
    submitUntil: Date;
    publish: Date,
}

export interface AssignmentCreatePacket {
    description: string;
    submitUntil: number;
    publish: number
}

export class AssignmentCreateModel {
    private readonly _submitUntil: number;
    private readonly _publish: number;

    public constructor(private _description: string, submitUntil: Date, publish: Date) {
        this._submitUntil = submitUntil.getTime();
        this._publish = publish.getTime();

        //TODO validate millis
    }

    public get submitUntil(): number {
        return this._submitUntil;
    }

    public get publish(): number {
        return this._publish;
    }

    public get description(): string {
        return this._description;
    }

    public static fromObject(obj: GenericAssignmentCreateModel): AssignmentCreateModel {
        return new AssignmentCreateModel(
            obj.description,
            obj.submitUntil,
            obj.publish
        );
    }

    public toPacket(): AssignmentCreatePacket {
        return {
            description: this.description,
            submitUntil: this.submitUntil,
            publish: this.publish
        };
    }
}
