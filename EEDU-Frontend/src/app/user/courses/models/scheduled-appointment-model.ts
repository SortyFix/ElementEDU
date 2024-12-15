import {DateInput, EventInput} from "@fullcalendar/core";
import {AppointmentEntryModel} from "./appointment-entry-model";
import {RRule} from "rrule";
import {ex} from "@fullcalendar/core/internal-common";

export class ScheduledAppointmentModel {

    public constructor(
        public readonly id: number, public readonly _start: number, public readonly _end: number, public readonly _duration: number, public readonly _period: number
    ) {}

    public static fromObject(object: any): ScheduledAppointmentModel {
        return new ScheduledAppointmentModel(
            object.id,
            object.start,
            object.end,
            object.duration,
            object.period
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

    public asEvent(name: string, entries: AppointmentEntryModel[]): EventInput {
        return {
            title: name,
            start: this.start.toISOString(),
            end: this.end.toISOString(),
            rrule: {
                freq: RRule.MINUTELY,
                interval: Number(this.period) / 60,
                dtstart: this.start.toISOString(),
                until: this.end.toISOString(),
            },
            exdate: entries.map((entry: AppointmentEntryModel): string => this.toDate(entry.timeStamp).toISOString())
        };
    }

    private toDate(timeStamp: number): Date {
        return new Date(timeStamp * 1000);
    }
}
