export class AppointmentCreateModel
{
    private readonly _start: number;

    public constructor(start: Date, private _duration: number, private _description?: string, private _assignment?: AppointmentCreateModel, /* TODO add assessment create model */ )
    {
        this._start = start.getTime();
    }

    public static fromObject(obj: { start: Date, duration: number, description?: string, assignment?: AppointmentCreateModel }): AppointmentCreateModel
    {
        return new AppointmentCreateModel(obj.start, obj.duration, obj.description, obj.assignment);
    }

    public get start(): number {
        return this._start;
    }

    public get duration(): number {
        return this._duration;
    }

    public get assignment(): AppointmentCreateModel | undefined {
        return this._assignment;
    }

    public get description(): string | undefined {
        return this._description;
    }
}
