import {Injectable} from '@angular/core';
import {Observable, of} from "rxjs";
import {GenericSubject, SubjectModel} from "./subject-model";
import {HttpClient} from "@angular/common/http";
import {CourseService} from "../course.service";
import {CourseModel} from "../course-model";
import {EntityService} from "../../../entity/entity-service";
import {CreateSubjectDialogComponent} from "./create-subject-dialog/create-subject-dialog.component";

@Injectable({
    providedIn: 'root'
})
export class SubjectService extends EntityService<string, SubjectModel, GenericSubject, GenericSubject> {

    public constructor(http: HttpClient, private readonly _courseService: CourseService)
    {
        super(http, 'course/subject', CreateSubjectDialogComponent);
    }

    public override translate(obj: GenericSubject): SubjectModel {
        return SubjectModel.fromObject(obj, (): Observable<readonly CourseModel[]> => this.fetchCoursesLazily([obj.id]));
    }

    public fetchCoursesLazily(subjects: string[]): Observable<readonly CourseModel[]> {
        if (this._courseService.fetched) {
            return of(this._courseService.findBySubjectLazily(subjects));
        }
        return this.fetchCourses(subjects);
    }

    public fetchCourses(subjects: string[]): Observable<readonly CourseModel[]> {
        const url: string = `${this.BACKEND_URL}/courses/${subjects.toString()}`;
        return this.http.get<any[]>(url, {withCredentials: true}).pipe(this._courseService.translateValue);
    }

    protected override postDelete(id: string[]): void {
        this._courseService.clearCache(); // courses having this subject also get deleted!
        super.postDelete(id);
    }
}
