
export interface GenericAppointmentCreateModel {
    start: Date,
    duration: number,
    room?: number,
    description?: string,
    assignment?: AppointmentCreateModel
}

export interface AppointmentCreatePacket {
    start: number,
    duration: number,
    description?: string,
    assignment?: AppointmentCreateModel
}

export class AppointmentCreateModel
{

    public constructor(
        private readonly _start: Date,
        private readonly _duration: number,
        private readonly _room?: number,
        private readonly _description?: string,
        private readonly _assignment?: AppointmentCreateModel
    ) {}

    public static fromObject(obj: GenericAppointmentCreateModel): AppointmentCreateModel
    {
        return new AppointmentCreateModel(
            obj.start,
            obj.duration,
            obj.room,
            obj.description,
            obj.assignment
        );
    }

    public get toPacket(): AppointmentCreatePacket
    {
        return {
            start: this.start.getTime(),
            duration: this.duration,
            description: this.description,
            assignment: this.assignment
        };
    }

    public get start(): Date {
        return this._start;
    }

    public get duration(): number {
        return this._duration;
    }

    public get assignment(): AppointmentCreateModel | undefined {
        return this._assignment;
    }

    public get room(): number | undefined {
        return this._room;
    }

    public get description(): string | undefined {
        return this._description;
    }
}
