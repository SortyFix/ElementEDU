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

    constructor(private http: HttpClient) { }

    public fetchCourses(): Observable<CourseModel[]> {
        const url = `${this.BACKEND_URL}/course/get/courses/`
        return this.http.get<any[]>(url, {withCredentials: true}).pipe(map((courses: any[]): CourseModel[] => {
            return courses.map((course: any): CourseModel => CourseModel.fromObject(course));
        }));
    }

}
