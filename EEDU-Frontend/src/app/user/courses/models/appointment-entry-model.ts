import {EventInput} from "@fullcalendar/core";

export class AppointmentEntryModel {

    constructor(public readonly id: number,
                public readonly _attachedScheduled: number,
                public readonly _start: number,
                private readonly _duration: number,
                public readonly description: string,
                public readonly homework: string) {}

    public isPart(id: number): boolean {
        return this._attachedScheduled == id;
    }

    public get start(): Date {
        return new Date(this._start);
    }

    private get duration(): number {
        return this._duration;
    }

    public static fromObject(object: any): AppointmentEntryModel {
        return new AppointmentEntryModel(
            object.id,
            object.attachedScheduled,
            object.start,
            object.end,
            object.description,
            object.homework
        );
    }

    public asEvent(name: string): EventInput
    {
        return {
            title: name,
            start: this.start,
            duration: this.duration,
        }
    }
}
