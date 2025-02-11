import {SubjectModel} from "./subject/subject-model";

export interface GenericCourseCreateModel
{
    name: string;
    subject: { id: number };
    clazz?: { id: number };
    users?: { id: bigint }[];
}

export interface CourseCreatePacket
{
    name: string;
    subjectId: number;
    classId?: number;
    users?: bigint[];
}

export class CourseCreateModel {

    public constructor(
        private readonly _name: string,
        private readonly _subjectId: number,
        private readonly _classId?: number,
        private readonly _users: bigint[] = []
    ) {}

    public static fromObject(obj: GenericCourseCreateModel): CourseCreateModel {
        return new CourseCreateModel(
            obj.name,
            obj.subject.id,
            obj.clazz?.id,
            obj.users?.map((current: {id: bigint}): bigint => { return current.id; }) || []);
    }

    public get toPacket(): CourseCreatePacket
    {
        return {
            name: this.name, subjectId: this.subjectId, classId: this.classId, users: this.users
        }
    }

    public get name(): string {
        return this._name;
    }

    public get subjectId(): number {
        return this._subjectId;
    }

    public get classId(): number | undefined {
        return this._classId;
    }

    public get users(): bigint[] {
        return this._users;
    }
}
