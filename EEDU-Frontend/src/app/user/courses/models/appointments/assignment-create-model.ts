export class AssignmentCreateModel
{
    private readonly _submitUntil: number;
    private readonly _publish: number;

    public constructor(private _description: string, submitUntil: Date, publish: Date) {
        this._submitUntil = submitUntil.getMilliseconds();
        this._publish = publish.getMilliseconds();

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
}
