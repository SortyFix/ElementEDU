import {Observable} from "rxjs";
import {CourseModel} from "../course-model";

export interface GenericSubject {id: string;}

export class SubjectModel {

    constructor(
        private readonly _id: string,
        private readonly _fetchCourses: () => Observable<readonly CourseModel[]>,
    ) {}

    public get id(): string {
        return this._id;
    }

    public get courses(): Observable<readonly CourseModel[]> {
        return this._fetchCourses();
    }

    public static fromObject(object: GenericSubject, fetchCourse: () => Observable<readonly CourseModel[]>): SubjectModel {
        return new SubjectModel(object.id, fetchCourse);
    }
}
