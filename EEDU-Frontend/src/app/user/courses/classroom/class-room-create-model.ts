export interface GenericClassRoomCreateModel {
    name: string;
    students?: { id: bigint }[],
    courses?: { id: bigint }[],
    tutor?: { id: bigint },
}

export interface ClassRoomCreatePacket { name: string; students: bigint[]; courses: bigint[]; tutor?: bigint }

export class ClassRoomCreateModel {

    public constructor(
        private readonly _name: string,
        private readonly _students: bigint[],
        private readonly _courses: bigint[],
        private readonly _tutor?: bigint,
    ) {}

    public static fromObject(obj: GenericClassRoomCreateModel): ClassRoomCreateModel {

        return new ClassRoomCreateModel(
            obj.name,
            obj.students?.map((item: { id: bigint }): bigint => { return item.id }) || [],
            obj.courses?.map((item: { id: bigint }): bigint => { return item.id }) || [],
            obj.tutor?.id
        )
    }

    public get toPacket(): ClassRoomCreatePacket
    {
        return {
            name: this.name,
            tutor: this.tutor,
            students: this.students,
            courses: this.courses
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
}
