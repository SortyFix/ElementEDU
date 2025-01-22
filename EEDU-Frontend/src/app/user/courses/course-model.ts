import {AppointmentEntryModel, GenericAppointmentEntry} from "./appointment/entry/appointment-entry-model";
import {FrequentAppointmentModel, GenericFrequentAppointment} from "./appointment/frequent/frequent-appointment-model";
import {GenericSubject, SubjectModel} from "./subject/subject-model";

export interface GenericCourse {
    id: bigint;
    name: string;
    subject: GenericSubject,
    appointmentEntries: GenericAppointmentEntry[],
    frequentAppointments: GenericFrequentAppointment[]
}

export class CourseModel {

    constructor(
        private readonly _id: bigint,
        private readonly _name: string,
        private readonly _subject: SubjectModel,
        private _appointmentEntries: readonly AppointmentEntryModel[],
        private _frequentAppointments: readonly FrequentAppointmentModel[]) {}

    private static getEntries(obj: GenericAppointmentEntry[]): AppointmentEntryModel[]
    {
        return obj.map((entry: GenericAppointmentEntry): AppointmentEntryModel => AppointmentEntryModel.fromObject(entry));
    }

    public static fromObject(object: GenericCourse): CourseModel
    {
        const course: CourseModel = new CourseModel(
            BigInt(object.id),
            object.name,
            SubjectModel.fromObject(object.subject),
            this.getEntries(object.appointmentEntries),
            object.frequentAppointments.map((entry: any): FrequentAppointmentModel =>
            {
                // when this method is called,
                // the course will already be created
                // and can therefore safely be returned here

                return FrequentAppointmentModel.fromObject(entry, (): CourseModel => course);
            })
        );
        return course;
    }

    public attachAppointment(appointment: AppointmentEntryModel): void
    {
        let replaced: boolean = false;
        this._appointmentEntries = this.appointmentEntries.map((item: AppointmentEntryModel): AppointmentEntryModel => {
            if (item.id !== appointment.id) {
                return item;
            }
            replaced = true;
            return appointment;
        });

        if (replaced) {
            return;
        }

        this._appointmentEntries = [...this._appointmentEntries, appointment]
    }

    public attachFrequentAppointment(frequentAppointmentModel: FrequentAppointmentModel): void
    {
        let replaced: boolean = false;
        this._frequentAppointments = this.frequentAppointments.map((item: FrequentAppointmentModel): FrequentAppointmentModel => {
            if (item.id !== frequentAppointmentModel.id) {
                return item;
            }
            replaced = true;
            return frequentAppointmentModel;
        });

        if (!replaced) {
            this._frequentAppointments = [...this._frequentAppointments, frequentAppointmentModel];
        }
    }

    public get id(): bigint {
        return this._id;
    }

    public get name(): string {
        return this._name;
    }

    public get subject(): SubjectModel {
        return this._subject;
    }

    public get appointmentEntries(): readonly AppointmentEntryModel[] {
        return this._appointmentEntries;
    }

    public get frequentAppointments(): readonly FrequentAppointmentModel[] {
        return this._frequentAppointments;
    }
}
