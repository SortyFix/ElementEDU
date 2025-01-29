export interface GenericClassRoomCreateModel {
    name: string;
    users?: { id: bigint }[],
    courses?: { id: bigint }[],
}

export interface ClassRoomCreatePacket { name: string; users: bigint[]; courses: bigint[]; }

export class ClassRoomCreateModel {

    public constructor(
        private readonly _name: string,
        private readonly _users: bigint[],
        private readonly _courses: bigint[],
    ) {}

    public static fromObject(obj: GenericClassRoomCreateModel): ClassRoomCreateModel {

        return new ClassRoomCreateModel(
            obj.name,
            obj.users?.map((item: { id: bigint }): bigint => { return item.id }) || [],
            obj.courses?.map((item: { id: bigint }): bigint => { return item.id }) || []
        )
    }

    public get toPacket(): ClassRoomCreatePacket
    {
        return {
            name: this._name,
            users: this._users,
            courses: this._courses
        }
    }

    public get name(): string {
        return this._name;
    }

    public get users(): bigint[] {
        return this._users;
    }

    public get courses(): bigint[] {
        return this._courses;
    }
}
