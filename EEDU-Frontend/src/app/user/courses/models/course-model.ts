import {SubjectModel} from "./subject-model";
import {AppointmentEntryModel} from "./appointments/appointment-entry-model";
import {ScheduledAppointmentModel} from "./appointments/scheduled-appointment-model";

export class CourseModel {

    constructor(public readonly id: number, public readonly name: string, public readonly subject: SubjectModel, public readonly appointmentEntries: AppointmentEntryModel[], public readonly scheduledAppointments: ScheduledAppointmentModel[]) {}

    public static fromObject(object: any): CourseModel {
        const id: number = object.id;
        const name: string = object.name;
        const subject: SubjectModel = SubjectModel.fromObject(object.subject);

        const entries: any = object.appointmentEntries;
        const appointmentEntries: AppointmentEntryModel[] = this.getEntries(entries);

        const scheduled: any = object.scheduledAppointments;
        const scheduledAppointments: ScheduledAppointmentModel[] = this.getScheduledAppointments(scheduled, appointmentEntries);

        return new CourseModel(id, name, subject, appointmentEntries, scheduledAppointments);
    }

    public addAppointment(appointment: AppointmentEntryModel): void
    {
        this.appointmentEntries.push(appointment);
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
