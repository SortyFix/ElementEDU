import {Injectable} from '@angular/core';
import {environment} from "../../../environment/environment";
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, map, Observable, tap} from "rxjs";
import {CourseModel} from "./course-model";

@Injectable({
    providedIn: 'root'
})
export class CourseService {

    private readonly BACKEND_URL: string = environment.backendUrl;
    private readonly _courseSubject: BehaviorSubject<CourseModel[]> = new BehaviorSubject<CourseModel[]>([]);

    constructor(private http: HttpClient) { }

    public fetchCourses(): Observable<CourseModel[]> {
        const url: string = `${this.BACKEND_URL}/course/get/all`;
        return this.http.get<any[]>(url, { withCredentials: true }).pipe(
            map((courses: any[]): CourseModel[] => courses.map((item: any): CourseModel => CourseModel.fromObject(item))),
            tap((courses: CourseModel[]): void => { this._courseSubject.next(courses); }),
        );
    }

    public findCourseLazily(id: bigint): CourseModel | undefined {
        return this.findCourse(this.courses, id);
    }

    public findCourse(courses: CourseModel[], id: bigint): CourseModel | undefined {
        return courses.find((course: CourseModel): boolean => course.id === id);
    }

    public get courses(): CourseModel[]
    {
        return this._courseSubject.value;
    }

    public get courses$(): Observable<CourseModel[]> {
        return this._courseSubject.asObservable();
    }

    public update(): void
    {
        this._courseSubject.next([...this.courses]);
    }
}
