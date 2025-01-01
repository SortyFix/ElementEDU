import {AppointmentEntryModel} from "./appointment-entry-model";
import {CalendarEvent} from "angular-calendar";

export class FrequentAppointmentModel {

    public constructor(
        public readonly id: number, public readonly _start: number, public readonly _end: number, public readonly _duration: number, public readonly _period: number, public readonly _attachedEntries: AppointmentEntryModel[]) {}

    public static fromObject(object: any, attachedEntries: AppointmentEntryModel[]): FrequentAppointmentModel {
        return new FrequentAppointmentModel(
            object.id,
            object.start,
            object.end,
            object.duration,
            object.period,
            attachedEntries
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

    private computeDuration(start: number): Date {
        return new Date(start + this.duration);
    }

    public asEvent(name: string): CalendarEvent[] {

        const events: CalendarEvent[] = [];

        for (let i: number = this._start; i <= this._end; i += this._period) {

            if(this._attachedEntries.some((current: AppointmentEntryModel): boolean => current._start === i))
            { // skip already created events
                continue;
            }

            const startDate: Date = new Date(i);
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
                    description: "I love trees"
                }
            });
        }
        return events;
    }

    private toDate(timeStamp: number): Date {
        return new Date(timeStamp * 1000);
    }
}
