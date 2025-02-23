export interface GenericClassRoomCreateModel {
    name: string;
    students?: { id: bigint }[],
    courses?: { id: bigint }[],
    tutor?: { id: bigint },
}

export interface ClassRoomCreatePacket {
    name: string;
    students: number[];
    courses: number[];
    tutor?: number
}

export class ClassRoomCreateModel {

    public constructor(
        private readonly _name: string,
        private readonly _students: bigint[],
        private readonly _courses: bigint[],
        private readonly _tutor?: bigint,
    ) {}

    public get toPacket(): ClassRoomCreatePacket {
        return {
            name: this.name,
            tutor: Number(this.tutor),
            students: this.students.map((id: bigint): number => Number(id)),
            courses: this.courses.map((id: bigint): number => Number(id)),
        }
    }

    public get name(): string {
        return this._name;
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

    public static fromObject(obj: GenericClassRoomCreateModel): ClassRoomCreateModel {

        return new ClassRoomCreateModel(
            obj.name,
            obj.students?.map((item: { id: bigint }): bigint => { return item.id }) || [],
            obj.courses?.map((item: { id: bigint }): bigint => { return item.id }) || [],
            obj.tutor?.id
        )
    }
}
