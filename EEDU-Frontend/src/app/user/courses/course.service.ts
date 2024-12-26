import {Injectable} from '@angular/core';
import {environment} from "../../../environment/environment";
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {CourseModel} from "./models/course-model";

@Injectable({
    providedIn: 'root'
})
export class CourseService {
    private readonly BACKEND_URL: string = environment.backendUrl;
    private readonly _courses: CourseModel[] = [];

    constructor(private http: HttpClient) { }

    public fetchCourses(): Observable<CourseModel[]> {
        const url = `${this.BACKEND_URL}/course/get/courses/`
        return this.http.get<any[]>(url, {withCredentials: true}).pipe(map((courses: any[]): CourseModel[] => {
            return courses.map((course: any): CourseModel =>
            {
                const courseModel: CourseModel = CourseModel.fromObject(course)
                this._courses.push(courseModel);
                return courseModel;
            });
        }));
    }

    public get courses(): CourseModel[] {
        if(this._courses.length === 0) {
            this.fetchCourses().subscribe();
        }
        return this._courses;
    }
}
