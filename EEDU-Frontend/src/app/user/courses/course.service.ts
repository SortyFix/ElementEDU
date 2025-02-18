import {Injectable} from '@angular/core';
import {BehaviorSubject, map, Observable, OperatorFunction, tap} from "rxjs";
import {CourseModel} from "./course-model";
import {GenericCourseCreateModel} from "./course-create-model";
import {ReducedUserModel} from "../reduced-user-model";
import {HttpClient} from "@angular/common/http";
import {AbstractCourseComponentsService} from "./abstract-course-components/abstract-course-components-service";

/**
 * Service for managing {@link CourseModel} instances.
 *
 * This service extends {@link AbstractCourseComponentsService} to provide functionalities
 * for handling courses, including retrieval and creation operations.
 *
 * @author Ivo Quiring
 */
@Injectable({
    providedIn: 'root'
})
export class CourseService extends AbstractCourseComponentsService<CourseModel, GenericCourseCreateModel> {

    private readonly _allSubject: BehaviorSubject<CourseModel[]> = new BehaviorSubject<CourseModel[]>([]);
    private _fetchedAdmin: boolean = false;

    public constructor(http: HttpClient) { super(http); }

    private get fetchAdminCourses(): Observable<CourseModel[]> {
        const url: string = `${this.BACKEND_URL}/course/get/all`;
        return this.http.get<any[]>(url, { withCredentials: true }).pipe(
            this.translate, tap((response: CourseModel[]): void =>
            {
                this._allSubject.next(response);
                this._fetchedAdmin = true;
            })
        );
    }

    public get fetchedAdmin(): boolean
    {
        return this._fetchedAdmin;
    }

    public get adminCourses$(): Observable<CourseModel[]> {
        if(!this.fetchedAdmin)
        {
            this.fetchAdminCourses.subscribe();
        }
        return this._allSubject.asObservable();
    }

    /**
     * Fetches the list of {@link ReducedUserModel} from a specific {@link CourseModel}.
     *
     * This method is used to fetch a list of {@link ReducedUserModel} that are part of the specified {@link CourseModel}.
     *
     * @param course the id of the course for which users should be fetched.
     * @returns an {@link Observable} that contains an array of {@link ReducedUserModel}.
     * @public
     */
    public fetchUsers(course: bigint): Observable<ReducedUserModel[]>
    {
        const url: string = `${this.BACKEND_URL}/get/users/${course}`;
        return this.http.get<ReducedUserModel[]>(url).pipe(map((user: any[]): ReducedUserModel[] =>
            user.map((item: any): ReducedUserModel => ReducedUserModel.fromObject(item))
        ));
    }

    /**
     * Finds a {@link CourseModel} by its id from the currently stored course list.
     *
     * This method attempts to find a {@link CourseModel} lazily using the internally stored list of courses.
     * Note that this does not work when {@link fetched} returns false. Use {@link fetchAll} first.
     *
     * @param id The id of the course to find.
     * @returns the matching {@link CourseModel} if found, otherwise undefined.
     * @public
     */
    public findCourseLazily(id: bigint): CourseModel | undefined {
        return this.findCourse(this.value, id);
    }

    /**
     * Finds a {@link CourseModel} by its id from a given list of courses.
     *
     * This method searches through the provided list of {@link CourseModel} instances and returns the one that matches
     * the given id.
     *
     * @param courses the list of {@link CourseModel} instances to search within.
     * @param id the id of the course to find.
     * @returns the matching {@link CourseModel} if found, otherwise undefined.
     * @public
     */
    public findCourse(courses: CourseModel[], id: bigint): CourseModel | undefined {
        return courses.find((course: CourseModel): boolean => course.id === id);
    }

    protected override get fetchAllValues(): Observable<any[]> {
        const url: string = `${this.BACKEND_URL}/course/get/courses`;
        return this.http.get<any[]>(url, { withCredentials: true });
    }

    protected override createValue(createModels: GenericCourseCreateModel[]): Observable<CourseModel[]> {
        const url: string = `${this.BACKEND_URL}/course/create`;
        return this.http.post<any[]>(url, createModels, { withCredentials: true });
    }

    protected override get translate(): OperatorFunction<any[], CourseModel[]> {
        return map((response: any[]): CourseModel[] => response.map((item: any): CourseModel => CourseModel.fromObject(item)));
    }
}
