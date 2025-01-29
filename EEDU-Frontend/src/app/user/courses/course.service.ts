import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {CourseModel} from "./course-model";
import {UserModel} from "../user-model";
import {AbstractSimpleCourseService} from "./abstract-simple-course-service";
import {GenericCourseCreateModel} from "./course-create-model";

@Injectable({
    providedIn: 'root'
})
export class CourseService extends AbstractSimpleCourseService<CourseModel, GenericCourseCreateModel>{

    constructor(http: HttpClient) { super(http); }

    protected override get fetchAllValues(): Observable<CourseModel[]> {
        const url: string = `${this.BACKEND_URL}/course/get/all`;
        return this.http.get<any[]>(url, { withCredentials: true }).pipe(
            map((courses: any[]): CourseModel[] => courses.map((item: any): CourseModel => CourseModel.fromObject(item))),
        );
    }

    protected override createValue(createModels: GenericCourseCreateModel[]): Observable<CourseModel[]> {
        const url: string = `${this.BACKEND_URL}/course/create`;
        return this.http.post<any[]>(url, createModels, { withCredentials:  true }).pipe(
            map((response: any[]): CourseModel[] => response.map((item: any): CourseModel => CourseModel.fromObject(item))),
        );
    }

    public fetchUsers(course: bigint): Observable<UserModel[]>
    {
        const url: string = `${this.BACKEND_URL}/get/users/${course}`;
        return this.http.get<UserModel[]>(url).pipe(
            map((user: any[]): UserModel[] => user.map((item: any): UserModel => UserModel.fromObject(item)))
        );
    }

    public findCourseLazily(id: bigint): CourseModel | undefined {
        return this.findCourse(this.value, id);
    }

    public findCourse(courses: CourseModel[], id: bigint): CourseModel | undefined {
        return courses.find((course: CourseModel): boolean => course.id === id);
    }
}
