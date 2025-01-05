import {CalendarEvent} from "angular-calendar";
import {AssignmentModel} from "./assignment-model";
import {RoomModel} from "../../room/room-model";

export class AppointmentEntryModel {

    constructor(private readonly _id: number,
                private readonly _start: number,
                private readonly _duration: number,
                private readonly _description: string = "No description has been set",
                private readonly _attachedScheduled?: number,
                private readonly _room?: RoomModel,
                public readonly _assignment?: AssignmentModel) {}

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

    public get id(): number {
        return this._id;
    }

    public get description(): string {
        return this._description;
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
            object.start,
            object.duration,
            object.description,
            object.attachedScheduled,
            object.room && RoomModel.fromObject(object.room),
            object.assignment && AssignmentModel.fromObject(object.assignment)
        );
    }

    public asEvent(name: string): CalendarEvent
    {
        return {
            id: this._id,
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
            meta: {
                type: AppointmentEntryModel,
                eventData: this
            }
        }
    }
}
