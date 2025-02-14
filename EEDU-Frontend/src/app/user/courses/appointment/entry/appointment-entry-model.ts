import {CalendarEvent} from "angular-calendar";
import {AssignmentModel, GenericAssignment} from "./assignment-model";
import {GenericRoom, RoomModel} from "../../room/room-model";

export interface GenericAppointmentEntry {
    id: bigint,
    duration: number,
    description: string,
    attachedScheduled: bigint,
    room: GenericRoom,
    assignment: GenericAssignment
}

export class AppointmentEntryModel {

    constructor(
        private readonly _id: bigint,
        private readonly _duration: number,
        private readonly _description: string = "No description has been set",
        private readonly _attachedScheduled?: bigint,
        private readonly _room?: RoomModel,
        public readonly _assignment?: AssignmentModel
    ) {}

    public static fromObject(object: GenericAppointmentEntry): AppointmentEntryModel {
        return new AppointmentEntryModel(
            BigInt(object.id),
            object.duration,
            object.description,
            object.attachedScheduled,
            object.room ? RoomModel.fromObject(object.room) : undefined,
            object.assignment ? AssignmentModel.fromObject(object.assignment) : undefined,
        );
    }

    public asEvent(name: string): CalendarEvent
    {
        return {
            id: Number(this.id),
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
                id: this.id,
                type: AppointmentEntryModel,
                eventData: this
            }
        }
    }

    public equalsStart(time: bigint): boolean {
        return this.timeStamp === time;
    }

    public hasAttached(): boolean
    {
        return !!this._attachedScheduled;
    }

    public isPart(id: bigint): boolean {
        return this._attachedScheduled == id;
    }

    public get id(): bigint {
        return this._id;
    }

    public get course(): bigint {
        // First 16 bits and mask them to get the id
        return (this.id >> 48n) & 0xFFFFn;
    }

    public get description(): string {
        return this._description;
    }

    public get start(): Date {
        return new Date(Number(this.timeStamp));
    }

    private get duration(): number {
        return this._duration;
    }

    public get end(): Date
    {
        return new Date(Number(this.timeStamp) + this.duration);
    }

    public get room(): RoomModel | undefined {
        return this._room;
    }

    public get assignment(): AssignmentModel | undefined {
        return this._assignment;
    }

    private get timeStamp(): bigint
    {
        // The upper 48 bits of the id that store the timestamp in 1/10 seconds.
        return (this.id & 0xFFFFFFFFFFFFn) * 100n;
    }
}
