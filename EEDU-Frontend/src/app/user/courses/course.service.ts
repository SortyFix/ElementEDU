import {Injectable} from '@angular/core';
import {BehaviorSubject, map, Observable, of, OperatorFunction, tap} from "rxjs";
import {CourseModel, GenericCourse} from "./course-model";
import {CourseCreateModel, CourseCreatePacket} from "./course-create-model";
import {ReducedUserModel} from "../reduced-user-model";
import {HttpClient} from "@angular/common/http";
import {AbstractCourseComponentsService} from "./abstract-course-components/abstract-course-components-service";
import {icons} from "../../../environment/styles";

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
export class CourseService extends AbstractCourseComponentsService<bigint, CourseModel, CourseCreateModel> {

    private readonly _allSubject: BehaviorSubject<CourseModel[]> = new BehaviorSubject<CourseModel[]>([]);

    public constructor(http: HttpClient) { super(http, icons.course); }

    private _fetchedAdmin: boolean = false;

    public get fetchedAdmin(): boolean {
        return this._fetchedAdmin;
    }

    public get adminCourses$(): Observable<CourseModel[]> {
        if (!this.fetchedAdmin) {
            this.fetchAdminCourses.subscribe();
        }
        return this._allSubject.asObservable();
    }

    public override get translate(): OperatorFunction<any[], CourseModel[]> {
        return map((response: any[]): CourseModel[] =>
            response.map((item: GenericCourse): CourseModel => CourseModel.fromObject(item, (): Observable<readonly CourseModel[]> =>
            {
                return of(this.findBySubjectLazily([item.subject.id]));
            }))
        );
    }

    protected override get fetchAllValues(): Observable<any[]> {
        const url: string = `${this.BACKEND_URL}/course/get/courses`;
        return this.http.get<any[]>(url, {withCredentials: true});
    }

    private get fetchAdminCourses(): Observable<CourseModel[]> {
        const url: string = `${this.BACKEND_URL}/course/get/all`;
        return this.http.get<any[]>(url, {withCredentials: true}).pipe(
            this.translate, tap((response: CourseModel[]): void => {
                this._allSubject.next(response);
                this._fetchedAdmin = true;
            })
        );
    }

    public findBySubjectLazily(subjects: string[]): readonly CourseModel[] {
        return this.value.filter((course: CourseModel): boolean => subjects.includes(course.subject.id));
    }

    public override clearCache(): void {
        super.clearCache();
        this._fetchedAdmin = false;
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
    public fetchUsers(course: bigint): Observable<ReducedUserModel[]> {
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
    public findCourseLazily(id: bigint): CourseModel | null {
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
    public findCourse(courses: CourseModel[], id: bigint): CourseModel | null {
        return courses.find((course: CourseModel): boolean => course.id === id) || null;
    }

    protected override pushCreated(response: CourseModel[]): void {
        super.pushCreated(response);
        this._allSubject.next([...this._allSubject.value, ...response]);
    }

    protected override createValue(createModels: CourseCreateModel[]): Observable<CourseModel[]> {
        const url: string = `${this.BACKEND_URL}/course/create`;
        return this.http.post<any[]>(url, this.toPackets(createModels), {withCredentials: true});
    }

    protected override deleteValue(id: bigint[]): Observable<void> {
        const url: string = `${this.BACKEND_URL}/course/delete/${id.toString()}`;
        return this.http.delete<void>(url, {withCredentials: true});
    }

    protected override postDelete(id: bigint[]): void {
        super.postDelete(id);
        this._allSubject.next(
            this._allSubject.value.filter(((value: CourseModel): boolean => !id.includes(value.id)))
        );
    }

    private toPackets(createModels: CourseCreateModel[]): CourseCreatePacket[] {
        return createModels.map((createModels: CourseCreateModel): CourseCreatePacket => createModels.toPacket);
    }
}
