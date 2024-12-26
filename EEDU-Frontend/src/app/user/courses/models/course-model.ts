import {SubjectModel} from "./subject-model";
import {AppointmentEntryModel} from "./appointments/appointment-entry-model";
import {ScheduledAppointmentModel} from "./appointments/scheduled-appointment-model";

export class CourseModel {

    constructor(public readonly id: bigint, public readonly name: string, public readonly subject: SubjectModel, public readonly appointmentEntries: AppointmentEntryModel[], public readonly scheduledAppointments: ScheduledAppointmentModel[]) {}

    public static fromObject(object: any): CourseModel {
        const id: bigint = BigInt(object.id);
        const name: string = object.name;
        const subject: SubjectModel = SubjectModel.fromObject(object.subject);

        const entries: any = object.appointmentEntries;
        const appointmentEntries: AppointmentEntryModel[] = this.getEntries(entries);

        const scheduled: any = object.scheduledAppointments;
        const scheduledAppointments: ScheduledAppointmentModel[] = this.getScheduledAppointments(scheduled, appointmentEntries);

        return new CourseModel(id, name, subject, appointmentEntries, scheduledAppointments);
    }

    public get isActive(): boolean
    {
        const currentDate = new Date();
        const futureAppointments: (entry: {end: Date}) => boolean
            = (entry: { end: Date }): boolean => entry.end > currentDate;

        return this.scheduledAppointments.some(futureAppointments) || this.appointmentEntries.some(futureAppointments);
    }

    private static getEntries(obj: any): AppointmentEntryModel[]
    {
        return obj.map((entry: any): AppointmentEntryModel => AppointmentEntryModel.fromObject(entry));
    }

    private static getScheduledAppointments(obj: any, entries: AppointmentEntryModel[]): ScheduledAppointmentModel[]
    {
        return obj.map((entry: any): ScheduledAppointmentModel =>
        {
            const filtered: AppointmentEntryModel[] = entries.filter((current: AppointmentEntryModel): boolean =>
            {
                return current.isPart(entry.id);
            });

            return ScheduledAppointmentModel.fromObject(entry, filtered)
        });
    }
}
