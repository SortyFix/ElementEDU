import {CalendarEvent} from "angular-calendar";
import {GenericRoom, RoomModel} from "../../room/room-model";
import {AppointmentEntryModel} from "../entry/appointment-entry-model";

export interface GenericFrequentAppointment {
    id: bigint,
    start: number,
    end: number,
    duration: number,
    period: number,
    room: GenericRoom,
}

export class FrequentAppointmentModel {

    public constructor(
        public readonly id: bigint,
        public readonly _start: number,
        public readonly _end: number,
        public readonly _duration: number,
        public readonly _period: number,
        public readonly _attachedEntries: AppointmentEntryModel[],
        private readonly _room: RoomModel,
    ) {}

    public static fromObject(
        object: GenericFrequentAppointment,
        attachedEntries: AppointmentEntryModel[]
    ): FrequentAppointmentModel {
        return new FrequentAppointmentModel(
            BigInt(object.id),
            object.start,
            object.end,
            object.duration,
            object.period,
            attachedEntries,
            RoomModel.fromObject(object.room),
        );
    }

    public get start(): Date {
        return this.toDate(this._start);
    }

    public get end(): Date {
        return this.toDate(this._end);
    }

    public get duration(): number {
        return this._duration;
    }

    public get period(): number {
        return this._period;
    }

    public get room(): RoomModel {
        return this._room;
    }

    public inPeriod(timeStamp: number): boolean {
        if (timeStamp < this._start || timeStamp > this._end) {
            return false;
        }

        const delta: number = timeStamp - this._start;
        return delta % this._period === 0;
    }

    public pushEvent(event: AppointmentEntryModel)
    {
        this._attachedEntries.push(event);
    }

    private computeDuration(start: number): Date {
        return new Date(start + this.duration);
    }

    public asEvent(name: string): CalendarEvent[] {

        const events: CalendarEvent[] = [];

        for (let i: number = this._start; i <= this._end; i += this._period) {

            const startDate: Date = new Date(i);
            const start: bigint = BigInt(i);

            if(this._attachedEntries.some((current: AppointmentEntryModel): boolean => current.equalsStart(start)))
            { // skip already created events
                continue;
            }

            events.push({
                title: name,
                start: startDate,
                end: this.computeDuration(i),
                resizable: {
                    beforeStart: false,
                    afterEnd: false,
                },
                draggable: false,
                meta: {
                    id: this.id,
                    type: FrequentAppointmentModel,
                    eventData: this
                }
            });
        }
        return events;
    }

    private toDate(timeStamp: number): Date {
        return new Date(timeStamp * 1000);
    }
}
