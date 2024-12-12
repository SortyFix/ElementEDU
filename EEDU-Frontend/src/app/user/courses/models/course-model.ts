import {AppointmentEntryModel} from "./appointment-entry-model";
import {ScheduledAppointmentModel} from "./scheduled-appointment-model";
import {SubjectModel} from "./subject-model";

export class CourseModel {

    constructor(public readonly id: bigint, public readonly name: string, public readonly subject: SubjectModel, public readonly entries: AppointmentEntryModel[], public readonly scheduledAppointments: ScheduledAppointmentModel[]) {}

    public static fromObject(object: any): CourseModel {
        const id: bigint = BigInt(object.id);
        const name: string = object.name;
        const subject: SubjectModel = SubjectModel.fromObject(object.subject);
        const entries: AppointmentEntryModel[] = object.entries.map((entry: any): AppointmentEntryModel => AppointmentEntryModel.fromObject(entry));
        const scheduledAppointments: ScheduledAppointmentModel[] = object.appointments.map((appointment: any): ScheduledAppointmentModel => ScheduledAppointmentModel.fromObject(appointment));

        return new CourseModel(id, name, subject, entries, scheduledAppointments);
    }
}
