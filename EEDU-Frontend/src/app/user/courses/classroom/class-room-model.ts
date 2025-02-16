import {ReducedUserModel} from "../../reduced-user-model";
import {CourseModel} from "../course-model";

export interface GenericClassRoomModel
{
    id: bigint;
    name: string;
    users: ReducedUserModel[];
    courses: CourseModel[];
}

export class ClassRoomModel {

    constructor(
        private readonly _id: bigint,
        private readonly _name: string,
        private readonly _users: ReducedUserModel[],
        private readonly _courses: CourseModel[]
    ) { }

    public static fromObject(obj: GenericClassRoomModel): ClassRoomModel
    {
        return new ClassRoomModel(obj.id, obj.name, obj.users || [], obj.courses || []);
    }

    public get id(): bigint {
        return this._id;
    }

    public get name(): string {
        return this._name;
    }

    public get users(): ReducedUserModel[] {
        return this._users;
    }

    public get courses(): CourseModel[] {
        return this._courses;
    }
}
