import {CalendarEvent} from "angular-calendar";
import {GenericRoom, RoomModel} from "../../room/room-model";
import {AppointmentEntryModel} from "../entry/appointment-entry-model";
import {CourseModel} from "../../course-model";

/**
 * Represents a generic frequent appointment data structure.
 *
 * This interface defines the structure of a frequent appointment.
 * It is needed in order to decode responses from the backend which itself communicates in JSON.
 */
export interface GenericFrequentAppointment {
    id: bigint,
    start: number,
    end: number,
    duration: number,
    frequency: number,
    room: GenericRoom,
}

/**
 * Represents a frequent appointment model.
 *
 * This class contains the data and behavior for frequent appointments.
 * @author Ivo Quiring
 */
export class FrequentAppointmentModel {

    /**
     * Constructs a new instance of {@link FrequentAppointmentModel}.
     *
     * @param _id the unique identifier of the frequent appointment.
     * @param _start the start time of the frequent appointment, in milliseconds.
     * @param _end the end time of the frequent appointment, in milliseconds.
     * @param _duration the duration of the frequent appointment, in milliseconds.
     * @param _frequency the frequency of the frequent appointment, in days.
     * @param _room the {@link RoomModel} instance representing the room of the appointment.
     * @param _course a function that returns a {@link CourseModel} instance associated with the appointment.
     */
    public constructor(
        private readonly _id: bigint,
        private readonly _start: number,
        private readonly _end: number,
        private readonly _duration: number,
        private readonly _frequency: number,
        private readonly _room: RoomModel,
        private readonly _course: () => CourseModel,
    ) {}

    /**
     * Retrieves the id of the frequent appointment.
     *
     * This getter method returns the unique identifier (_id) of the frequent appointment.
     *
     * @returns the unique identifier of the frequent appointment as a bigint.
     * @public
     */
    public get id(): bigint {
        return this._id;
    }

    /**
     * Retrieves the start time of the frequent appointment.
     *
     * This getter method converts the internal start time (_start) to a {@link Date}
     * and returns it for external use.
     *
     * @returns the start time of the frequent appointment as a {@link Date}.
     * @public
     */
    public get start(): Date {
        return this.toDate(this._start);
    }

    /**
     * Retrieves the end time of the frequent appointment.
     *
     * This getter method converts the internal end time (_end) to a {@link Date}
     * and returns it for external use.
     *
     * @returns The end time of the frequent appointment as a {@link Date}.
     * @public
     */
    public get end(): Date {
        return this.toDate(this._end);
    }

    /**
     * Retrieves the duration of the frequent appointment.
     *
     * This getter method returns the duration (_duration) of the frequent appointment in milliseconds.
     *
     * @returns The duration of the frequent appointment as milliseconds.
     * @public
     */
    public get duration(): number {
        return this._duration;
    }

    /**
     * Retrieves the period of the frequent appointment.
     *
     * This getter method returns the period (_period) of the frequent appointment. This is given in days.
     *
     * @returns the period of the frequent appointment given in days.
     * @public
     */
    public get frequency(): number {
        return this._frequency;
    }

    /**
     * Retrieves the room associated with the frequent appointment.
     *
     * This getter method returns the {@link RoomModel} instance (_room) that represents the room
     * where the frequent appointment takes place.
     *
     * @returns the room associated with the frequent appointment as a {@link RoomModel}.
     * @public
     */
    public get room(): RoomModel {
        return this._room;
    }

    /**
     * Retrieves the course associated with the frequent appointment.
     *
     * This getter calls the provided method and returns the {@link CourseModel}
     * instance that represents the course linked to the frequent appointment.
     *
     * @returns the course associated with the frequent appointment as a {@link CourseModel}.
     * @public
     */
    public get course(): CourseModel {
        return this._course();
    }

    /**
     * Retrieves all attached appointment entries linked to this frequent appointment.
     *
     * This getter filters the appointment entries attached with the course to return only
     * those entries that are part of this frequent appointment, determined by {@link AppointmentEntryModel.isPart()}.
     *
     * @returns an array of {@link AppointmentEntryModel} instances that are part of this frequent appointment.
     * @private
     */
    private get attachedEntries(): AppointmentEntryModel[] {
        return this.course.appointmentEntries.filter(((entity: AppointmentEntryModel): boolean => {
            return entity.isPart(this.id);
        }))
    }

    /**
     * Creates a new instance of {@link FrequentAppointmentModel} from a given object.
     *
     * This method converts a {@link GenericFrequentAppointment} into a {@link FrequentAppointmentModel}
     * by extracting and transforming its properties, including parsing the room and linking
     * the course through a provided function.
     *
     * @param object containing the generic frequent appointment data.
     * @param course a function that returns a {@link CourseModel} instance associated with the appointment.
     * @returns a new instance of {@link FrequentAppointmentModel} with the provided data.
     * @public
     */
    public static fromObject(
        object: GenericFrequentAppointment,
        course: () => CourseModel
    ): FrequentAppointmentModel {
        return new FrequentAppointmentModel(
            BigInt(object.id),
            object.start,
            object.end,
            object.duration,
            object.frequency,
            RoomModel.fromObject(object.room),
            course,
        );
    }

    /**
     * Converts the frequent appointment into an array of calendar events.
     *
     * This method generates calendar events for the frequent appointment within the defined start
     * and end time range. Events are skipped if an attached entry already exists for a specific date and time.
     *
     * @param name the name/title of the event.
     * @returns an array of {@link CalendarEvent} representing the frequent appointment as calendar events.
     * @public
     */
    public asEvent(name: string): CalendarEvent[] {

        const events: CalendarEvent[] = [];

        for (let i: number = this._start; i <= this._end; i += this.frequency) {

            const startDate: Date = new Date(i);

            if (this.attachedEntries.some((current: AppointmentEntryModel): boolean =>
                current.start.getUTCFullYear() == startDate.getUTCFullYear() &&
                current.start.getUTCDate() == startDate.getUTCDate() &&
                current.start.getUTCMonth() == startDate.getUTCMonth() &&
                current.start.getUTCMinutes() == startDate.getUTCMinutes()
            )) { continue; } // skip already created events

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

    /**
     * Computes the end date and time for an event based on its start time and duration.
     *
     * This utility method calculates the event's end time by adding the duration
     * to the given start timestamp.
     *
     * @param start the start timestamp of the event.
     * @returns the computed end time of the event as a {@link Date}.
     * @private
     */
    private computeDuration(start: number): Date {
        return new Date(start + this.duration);
    }

    /**
     * Converts a timestamp into a {@link Date}.
     *
     * This utility method takes a timestamp (in seconds) and converts it into a {@link Date}
     * for further use in the application.
     *
     * @param timeStamp the timestamp to convert (in seconds).
     * @returns the {@link Date} representation of the provided timestamp.
     * @private
     */
    private toDate(timeStamp: number): Date {
        return new Date(timeStamp * 1000);
    }
}
