import {Injectable} from '@angular/core';
import {map, Observable, of, OperatorFunction} from "rxjs";
import {GenericSubject, SubjectModel} from "./subject-model";
import {HttpClient} from "@angular/common/http";
import {AbstractCourseComponentsService} from "../abstract-course-components/abstract-course-components-service";
import {CourseService} from "../course.service";
import {icons} from "../../../../environment/styles";
import {CourseModel} from "../course-model";

@Injectable({
    providedIn: 'root'
})
export class SubjectService extends AbstractCourseComponentsService<string, SubjectModel, { id: string }> {

    public constructor(http: HttpClient,
            private readonly _courseService: CourseService
    ) { super(http, icons.subject) }

    public override get translate(): OperatorFunction<any[], SubjectModel[]> {
        return map((response: any[]): SubjectModel[] => response.map((item: GenericSubject): SubjectModel =>
            SubjectModel.fromObject(item, (): Observable<readonly CourseModel[]> => this.fetchCoursesLazily([item.id]))
        ));
    }

    public fetchCoursesLazily(subjects: string[]): Observable<readonly CourseModel[]>
    {
        if (this._courseService.fetched) {
            return of(this._courseService.findBySubjectLazily(subjects));
        }
        return this.fetchCourses(subjects)
    }

    public fetchCourses(subjects: string[]): Observable<readonly CourseModel[]>
    {
        const url = `${this.BACKEND_URL}/course/subject/courses/${subjects.toString()}`;
        return this.http.get<any[]>(url, {withCredentials: true}).pipe(this._courseService.translate);
    }

    protected override get fetchAllValues(): Observable<SubjectModel[]> {
        return this.http.get<any[]>(`${this.BACKEND_URL}/course/subject/get/all`, {withCredentials: true});
    }

    protected override createValue(models: { id: string }[]): Observable<SubjectModel[]> {
        return this.http.post<any[]>(`${this.BACKEND_URL}/course/subject/create`, models, {withCredentials: true});
    }

    protected override deleteValue(id: string[]): Observable<void> {
        const url: string = `${this.BACKEND_URL}/course/subject/delete/${id.toString()}`;
        return this.http.delete<void>(url, {withCredentials: true});
    }

    protected override postDelete(id: string[]): void {
        this._courseService.clearCache(); // courses having this subject also get deleted!
        super.postDelete(id);
    }
}
