export interface GenericCourseCreateModel
{
    name: string;
    subject: { id: number };
    teacher: { id: bigint }
    students: { id: bigint }[];
    classroom?: { id: number };
}

export interface CourseCreatePacket
{
    name: string;
    subject: number;
    teacher: number;
    students: number[];
    classroom: number | null;
}

export class CourseCreateModel {

    public constructor(
        private readonly _name: string,
        private readonly _subject: number,
        private readonly _teacher: bigint,
        private readonly _students: bigint[] = [],
        private readonly _classroom: number | null,
    ) {}

    public static fromObject(obj: GenericCourseCreateModel): CourseCreateModel {
        return new CourseCreateModel(
            obj.name,
            obj.subject.id,
            obj.teacher.id,
            obj.students?.map((current: {id: bigint}): bigint => { return current.id; }) || [],
            obj.classroom?.id || null
        );
    }

    public get toPacket(): CourseCreatePacket
    {
        return {
            name: this.name,
            subject: this.subject,
            teacher: Number(this.teacher),
            students: this.students.map((userId: bigint): number => Number(userId)),
            classroom: this.classroom
        }
    }

    public get name(): string {
        return this._name;
    }

    public get subject(): number {
        return this._subject;
    }

    public get teacher(): bigint {
        return this._teacher;
    }

    public get classroom(): number | null {
        return this._classroom;
    }

    public get students(): bigint[] {
        return this._students;
    }
}
