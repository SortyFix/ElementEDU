export class CourseCreateModel {

    public constructor(
        private readonly _name: string,
        private readonly _subjectId: number,
        private readonly _classId?: number,
        private readonly _users: number[] = []
    ) {}

    public static fromObject(obj: {
        name: string,
        subjectId: number,
        classId?: number,
        users: number[]
    }): CourseCreateModel {
        return new CourseCreateModel(obj.name, obj.subjectId, obj.classId, obj.users);
    }

    public get toPacket(): { name: string; subjectId: number, classId?: number, users: number[] }
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

    public get users(): number[] {
        return this._users;
    }
}
