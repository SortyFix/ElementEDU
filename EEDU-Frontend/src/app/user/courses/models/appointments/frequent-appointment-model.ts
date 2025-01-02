import {AppointmentEntryModel} from "./appointment-entry-model";
import {CalendarEvent} from "angular-calendar";
import {RoomModel} from "../../room/room-model";

export class FrequentAppointmentModel {

    public constructor(
        public readonly id: number,
        public readonly _start: number,
        public readonly _end: number,
        public readonly _duration: number,
        public readonly _period: number,
        public readonly _attachedEntries: AppointmentEntryModel[],
        private readonly _room?: RoomModel,
    ) {}

    public static fromObject(object: any, attachedEntries: AppointmentEntryModel[]): FrequentAppointmentModel {
        return new FrequentAppointmentModel(
            object.id,
            object.start,
            object.end,
            object.duration,
            object.period,
            attachedEntries,
            object.room,
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

    get period(): number {
        return this._period;
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
            if(this._attachedEntries.some((current: AppointmentEntryModel): boolean => current.equalsStart(i)))
            { // skip already created events
                continue;
            }

            events.push({
                id: this.id,
                title: name,
                start: startDate,
                end: this.computeDuration(i),
                resizable: {
                    beforeStart: false,
                    afterEnd: false,
                },
                draggable: false,
                meta: {
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
