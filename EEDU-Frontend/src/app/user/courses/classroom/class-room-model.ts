import {GenericReducedUserModel, ReducedUserModel} from "../../reduced-user-model";

export interface GenericClassRoomModel {
    id: string;
    tutor: GenericReducedUserModel;
    students: GenericReducedUserModel[];
}

export class ClassRoomModel {

    constructor(
        private readonly _id: string,
        private readonly _tutor: ReducedUserModel,
        private readonly _students: ReducedUserModel[],
    ) { }

    public get id(): string {
        return this._id;
    }

    public get students(): ReducedUserModel[] {
        return this._students;
    }

    public get tutor(): ReducedUserModel {
        return this._tutor;
    }

    public static fromObject(obj: GenericClassRoomModel): ClassRoomModel {
        return new ClassRoomModel(obj.id, ReducedUserModel.fromObject(obj.tutor), obj.students.map(
            (item: GenericReducedUserModel): ReducedUserModel => ReducedUserModel.fromObject(item)
        ));
    }
}
