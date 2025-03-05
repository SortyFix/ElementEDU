import {Injectable} from '@angular/core';
import {BehaviorSubject, map, Observable, of, tap} from "rxjs";
import {CourseModel, GenericCourse} from "./course-model";
import {CourseCreateModel} from "./course-create-model";
import {ReducedUserModel} from "../reduced-user-model";
import {HttpClient} from "@angular/common/http";
import {EntityService} from "../../entity/entity-service";

/**
 * Service for managing {@link CourseModel} instances.
 *
 * This service extends {@link EntityService} to provide functionalities
 * for handling courses, including retrieval and creation operations.
 *
 * @author Ivo Quiring
 */
@Injectable({
    providedIn: 'root'
})
export class CourseService extends EntityService<bigint, CourseModel, GenericCourse, CourseCreateModel> {

    private _fetchedOwn: boolean = false;
    private readonly _ownCourses: BehaviorSubject<CourseModel[]> = new BehaviorSubject<CourseModel[]>([]);

    public constructor(http: HttpClient) { super(http, 'course'); }

    public override translate(obj: GenericCourse): CourseModel {
        return CourseModel.fromObject(obj, (): Observable<readonly CourseModel[]> => {
            return of(this.findBySubjectLazily([obj.subject.id]));
        });
    }

    public get fetchedOwn(): boolean {
        return this._fetchedOwn;
    }

    public get ownCourses$(): Observable<CourseModel[]> {
        if (!this.fetchedOwn) {
            this.fetchOwnCourses.subscribe();
        }
        return this._ownCourses.asObservable();
    }

    private get fetchOwnCourses(): Observable<CourseModel[]> {
        const url: string = `${this.BACKEND_URL}/get`;
        return this.http.get<any[]>(url, {withCredentials: true}).pipe(this.translateValue, tap((response: CourseModel[]): void => {
            this._ownCourses.next(response);
            this._fetchedOwn = true;
        }));
    }

    public findBySubjectLazily(subjects: string[]): readonly CourseModel[] {
        return this.value.filter((course: CourseModel): boolean => {
            return subjects.includes(course.subject.id);
        });
    }

    public override clearCache(): void {
        super.clearCache();
        this._fetchedOwn = false;
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
        return this.http.get<ReducedUserModel[]>(url).pipe(map((user: any[]): ReducedUserModel[] => user.map((item: any): ReducedUserModel => ReducedUserModel.fromObject(item))));
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
        this._ownCourses.next([...this._ownCourses.value, ...response]);
    }

    protected override postDelete(id: bigint[]): void {
        super.postDelete(id);
        this._ownCourses.next(this._ownCourses.value.filter((value: CourseModel): boolean => !id.includes(value.id)));
    }
}
