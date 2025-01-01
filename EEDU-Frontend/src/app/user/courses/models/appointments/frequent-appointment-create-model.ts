import {RoomModel} from "../../room/room-model";

export class FrequentAppointmentCreateModel {

    private readonly _start: number;
    private readonly _until: number;

    public constructor(start: Date, until: Date, private room: RoomModel, private readonly _duration: number, private readonly _frequency: number) {
        this._start = start.getTime();
        this._until = until.getTime();
    }

    public static fromObject(obj: { start: Date, until: Date, room: RoomModel, duration: number, frequency: number }): FrequentAppointmentCreateModel
    {
        return new FrequentAppointmentCreateModel(obj.start, obj.until, obj.room, obj.duration, obj.frequency);
    }

    public get start(): number {
        return this._start;
    }

    public get until(): number {
        return this._until;
    }

    public get duration(): number {
        return this._duration;
    }

    public get frequency(): number {
        return this._frequency;
    }

    public get toPacket(): { start: number, until: number, room: number, duration: number, frequency: number}
    {
        return { start: this.start, until: this.until, room: this.room.id, duration: this.duration, frequency: this.frequency };
    }

}
