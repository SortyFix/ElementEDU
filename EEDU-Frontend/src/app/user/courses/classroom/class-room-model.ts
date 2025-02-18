import {GenericReducedUserModel, ReducedUserModel} from "../../reduced-user-model";
import {CourseModel} from "../course-model";

export interface GenericClassRoomModel
{
    id: bigint;
    name: string;
    tutor: GenericReducedUserModel;
    students: GenericReducedUserModel[];
    courses: CourseModel[];
}

export class ClassRoomModel {

    constructor(
        private readonly _id: bigint,
        private readonly _name: string,
        private readonly _tutor: ReducedUserModel,
        private readonly _students: ReducedUserModel[],
        private readonly _courses: CourseModel[]
    ) { }

    public static fromObject(obj: GenericClassRoomModel): ClassRoomModel
    {
        return new ClassRoomModel(obj.id, obj.name, ReducedUserModel.fromObject(obj.tutor), obj.students.map(
            (item: GenericReducedUserModel): ReducedUserModel => ReducedUserModel.fromObject(item)
        ), obj.courses);
    }

    public get id(): bigint {
        return this._id;
    }

    public get name(): string {
        return this._name;
    }

    public get students(): ReducedUserModel[] {
        return this._students;
    }

    public get tutor(): ReducedUserModel {
        return this._tutor;
    }

    public get courses(): CourseModel[] {
        return this._courses;
    }
}
