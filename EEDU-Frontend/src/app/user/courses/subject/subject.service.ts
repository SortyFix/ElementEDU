import { Injectable } from '@angular/core';
import {map, Observable, OperatorFunction} from "rxjs";
import {SubjectModel} from "./subject-model";
import {HttpClient} from "@angular/common/http";
import {AbstractCourseComponentsService} from "../abstract-course-components/abstract-course-components-service";

@Injectable({
    providedIn: 'root'
})
export class SubjectService extends AbstractCourseComponentsService<SubjectModel, { name: string }>
{
    public constructor(http: HttpClient) { super(http) }

    protected override get fetchAllValues(): Observable<SubjectModel[]> {
        return this.http.get<any[]>(`${this.BACKEND_URL}/course/subject/get/all`, { withCredentials: true });
    }

    protected createValue(models: { name: string }[]): Observable<SubjectModel[]> {
        return this.http.post<any[]>(`${this.BACKEND_URL}/course/subject/create`, models, { withCredentials: true });
    }

    protected override deleteValue(id: number[]): Observable<void> {
        const url: string = `${this.BACKEND_URL}/course/subject/delete/${id.toString()}`;
        return this.http.delete<void>(url, { withCredentials: true });
    }

    protected override get translate(): OperatorFunction<any[], SubjectModel[]> {
        return map((response: any[]): SubjectModel[] =>
            response.map((item: any): SubjectModel => SubjectModel.fromObject(item))
        );
    }
}
