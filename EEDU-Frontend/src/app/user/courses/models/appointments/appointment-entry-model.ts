import {CalendarEvent} from "angular-calendar";
import {AssignmentModel} from "./assignment-model";

export class AppointmentEntryModel {

    constructor(public readonly id: number,
                private readonly _attachedScheduled: number | null,
                private readonly _start: number,
                private readonly _duration: number,
                private readonly _description: string,
                public readonly assignment: AssignmentModel | null) {}

    public equalsStart(time: number): boolean {
        return this._start === time;
    }

    public hasAttached(): boolean
    {
        return !!this._attachedScheduled;
    }

    public isPart(id: number): boolean {
        return this._attachedScheduled == id;
    }

    public get start(): Date {
        return new Date(this._start);
    }

    private get duration(): number {
        return this._duration;
    }

    public get end(): Date
    {
        return new Date(this._start + this.duration);
    }

    public static fromObject(object: any): AppointmentEntryModel {
        return new AppointmentEntryModel(
            object.id,
            object.attachedScheduled,
            object.start,
            object.duration,
            object.description,
            object.assignment && AssignmentModel.fromObject(object.assignment)
        );
    }

    public asEvent(name: string): CalendarEvent
    {
        return {
            id: this.id,
            title: name,
            start: this.start,
            end: this.end,
            resizable: {
                beforeStart: false,
                afterEnd: false
            },
            color: {
                primary: '#f00',
                secondary: '#0f0',
            },
            draggable: false,
        }
    }
}
