export interface GenericClassRoomCreateModel {
    id: string;
    tutor: { id: bigint },
    students?: { id: bigint }[],
    courses?: { id: bigint }[],
}

export interface ClassRoomCreatePacket {
    id: string;
    students: number[];
    courses: number[];
    tutor?: number
}

export class ClassRoomCreateModel {

    public constructor(
        private readonly _id: string,
        private readonly _tutor: bigint,
        private readonly _students: bigint[],
        private readonly _courses: bigint[],
    ) {}

    public static fromObject(obj: GenericClassRoomCreateModel): ClassRoomCreateModel {
        return new ClassRoomCreateModel(
            obj.id,
            obj.tutor.id,
            obj.students?.map((item: { id: bigint }): bigint => { return item.id }) || [],
            obj.courses?.map((item: { id: bigint }): bigint => { return item.id }) || [],
        )
    }

    public get toPacket(): ClassRoomCreatePacket {
        return {
            id: this.id,
            tutor: Number(this.tutor),
            students: this.students.map((id: bigint): number => Number(id)),
            courses: this.courses.map((id: bigint): number => Number(id)),
        }
    }

    public get id(): string {
        return this._id;
    }

    public get tutor(): bigint | undefined {
        return this._tutor;
    }

    public get students(): bigint[] {
        return this._students;
    }

    public get courses(): bigint[] {
        return this._courses;
    }
}
