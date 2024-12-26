export class AppointmentCreateModel
{
    private readonly _start: number;
    private readonly _duration: number;

    public constructor(start: Date, end: Date, private _description?: string, private _assignment?: AppointmentCreateModel, /* TODO add assessment create model */ )
    {
        const startMillis: number = start.getTime();
        const endMillis: number = end.getTime();

        if(startMillis > endMillis)
        {
            throw new Error("start millis must be smaller than end millis");
        }

        this._start = startMillis;
        this._duration = endMillis - startMillis;
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
