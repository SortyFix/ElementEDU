import {EventInput} from "@fullcalendar/core";
import {AppointmentEntryModel} from "./appointment-entry-model";
import {RRule} from "rrule";

export class ScheduledAppointmentModel {

    public constructor(
        public readonly id: number, public readonly _start: number, public readonly _end: number, public readonly _duration: number, public readonly _period: number, public readonly _attachedEntries: AppointmentEntryModel[]) {}

    public static fromObject(object: any, attachedEntries: AppointmentEntryModel[]): ScheduledAppointmentModel {
        return new ScheduledAppointmentModel(
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

    public asEvent(name: string): EventInput {

        const totalSeconds = Math.floor(this.duration / 1000); // Convert millis to seconds
        const hours = Math.floor(totalSeconds / 3600); // Calculate hours
        const minutes = Math.floor((totalSeconds % 3600) / 60); // Calculate minutes
        const seconds = totalSeconds % 60; // Remaining seconds

        // Format each component to ensure it's two digits
        const formattedTime = [
            hours.toString().padStart(2, '0'),
            minutes.toString().padStart(2, '0'),
            seconds.toString().padStart(2, '0')
        ].join(':');

        console.log(formattedTime);

        return {
            title: name,
            start: this.start,
            end: this.end,
            duration: formattedTime,
            rrule: {
                freq: RRule.MINUTELY,
                interval: Number(this.period) / 60,
                dtstart: this.start.toISOString(),
                until: this.end.toISOString(),
            },
            exdate: this._attachedEntries.map((entry: AppointmentEntryModel): string =>
            {
                return entry.start.toISOString();
            })
        };
    }

    private toDate(timeStamp: number): Date {
        return new Date(timeStamp * 1000);
    }



}
