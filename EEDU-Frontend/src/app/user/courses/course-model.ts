import {AppointmentEntryModel} from "./appointment/entry/appointment-entry-model";
import {FrequentAppointmentModel} from "./appointment/frequent/frequent-appointment-model";
import {SubjectModel} from "./subject/subject-model";

export class CourseModel {

    constructor(public readonly id: number, public readonly name: string, public readonly subject: SubjectModel, public readonly appointmentEntries: AppointmentEntryModel[], public readonly frequentAppointments: FrequentAppointmentModel[]) {}

    public static fromObject(object: any): CourseModel {
        const id: number = object.id;
        const name: string = object.name;
        const subject: SubjectModel = SubjectModel.fromObject(object.subject);

        const entries: any = object.appointmentEntries;
        const appointmentEntries: AppointmentEntryModel[] = this.getEntries(entries);

        const frequent: any = object.frequentAppointments;
        const frequentAppointments: FrequentAppointmentModel[] = this.getFrequentAppointments(frequent, appointmentEntries);

        return new CourseModel(id, name, subject, appointmentEntries, frequentAppointments);
    }

    private static getEntries(obj: any): AppointmentEntryModel[]
    {
        return obj.map((entry: any): AppointmentEntryModel => AppointmentEntryModel.fromObject(entry));
    }

    public attachAppointment(appointment: AppointmentEntryModel): void
    {
        this.appointmentEntries.push(appointment);

        if(appointment.hasAttached())
        {
            this.frequentAppointments.find(
                (event: FrequentAppointmentModel): boolean => appointment.isPart(event.id)
            )?.pushEvent(appointment);
        }
    }

    public attachFrequentAppointment(frequentAppointmentModel: FrequentAppointmentModel): void
    {
        this.frequentAppointments.push(frequentAppointmentModel);
    }

    private static getFrequentAppointments(obj: any, entries: AppointmentEntryModel[]): FrequentAppointmentModel[]
    {
        return obj.map((entry: any): FrequentAppointmentModel =>
        {
            const filtered: AppointmentEntryModel[] = entries.filter((current: AppointmentEntryModel): boolean =>
            {
                return current.isPart(entry.id);
            });

            return FrequentAppointmentModel.fromObject(entry, filtered)
        });
    }
}
