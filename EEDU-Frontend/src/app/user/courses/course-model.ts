import {AppointmentEntryModel, GenericAppointmentEntry} from "./appointment/entry/appointment-entry-model";
import {FrequentAppointmentModel, GenericFrequentAppointment} from "./appointment/frequent/frequent-appointment-model";
import {GenericSubject, SubjectModel} from "./subject/subject-model";
import {ClassRoomModel, GenericClassRoom} from "./classroom/class-room-model";
import {GenericReducedUserModel, ReducedUserModel} from "../reduced-user-model";
import {Observable} from "rxjs";

export interface GenericCourse {
    id: bigint;
    name: string;
    subject: GenericSubject;
    students: GenericReducedUserModel[],
    appointmentEntries: GenericAppointmentEntry[];
    frequentAppointments: GenericFrequentAppointment[];
    teacher?: GenericReducedUserModel,
    classRoom?: GenericClassRoom;
}

export class CourseModel {

    public constructor(private readonly _id: bigint, private readonly _name: string, private readonly _subject: SubjectModel, private _students: readonly ReducedUserModel[], private _appointmentEntries: readonly AppointmentEntryModel[], private _frequentAppointments: readonly FrequentAppointmentModel[], private readonly _teacher: ReducedUserModel | null, private readonly _classRoom: ClassRoomModel | null,) {}

    public get id(): bigint {
        return this._id;
    }

    public get name(): string {
        return this._name;
    }

    public get subject(): SubjectModel {
        return this._subject;
    }

    public get students(): readonly ReducedUserModel[] {

        if (this.classRoom != null) {
            const deduplicated = new Map();

            [...this.classRoom.students, ...this.ownStudents].forEach(student => {
                deduplicated.set(student.id, student);
            });

            return Array.from(deduplicated.values());
        }

        return this.ownStudents;
    }

    public isOwnStudent(student: ReducedUserModel): boolean
    {
        return this.ownStudents.includes(student);
    }

    public get ownStudents(): readonly ReducedUserModel[] {
        return this._students;
    }

    public get appointmentEntries(): readonly AppointmentEntryModel[] {
        return this._appointmentEntries;
    }

    public get frequentAppointments(): readonly FrequentAppointmentModel[] {
        return this._frequentAppointments;
    }

    public get teacher(): ReducedUserModel | null {
        return this._teacher;
    }

    public get classRoom(): ClassRoomModel | null {
        return this._classRoom;
    }

    public static fromObject(object: GenericCourse, findBySubject: () => Observable<readonly CourseModel[]>): CourseModel {
        const course: CourseModel = new CourseModel(BigInt(object.id), object.name, SubjectModel.fromObject(object.subject, findBySubject), (object.students || []).map((student: GenericReducedUserModel): ReducedUserModel => ReducedUserModel.fromObject(student)), this.getEntries(object.appointmentEntries), object.frequentAppointments.map((entry: any): FrequentAppointmentModel => {
            // when this method is called,
            // the course will already be created
            // and can therefore safely be returned here

            return FrequentAppointmentModel.fromObject(entry, (): CourseModel => course);
        }), object.teacher ? ReducedUserModel.fromObject(object.teacher) : null, object.classRoom ? ClassRoomModel.fromObject(object.classRoom) : null);
        return course;
    }

    private static getEntries(obj: GenericAppointmentEntry[]): AppointmentEntryModel[] {
        return obj.map((entry: GenericAppointmentEntry): AppointmentEntryModel => AppointmentEntryModel.fromObject(entry));
    }

    public attachAppointment(appointment: AppointmentEntryModel): void {
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

    public attachFrequentAppointment(frequentAppointmentModel: FrequentAppointmentModel): void {
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
}
