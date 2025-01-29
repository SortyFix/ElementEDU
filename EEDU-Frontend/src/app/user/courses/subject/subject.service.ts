import { Injectable } from '@angular/core';
import {map, Observable, OperatorFunction, tap} from "rxjs";
import {SubjectModel} from "./subject-model";
import {HttpClient} from "@angular/common/http";
import {AbstractSimpleCourseService} from "../abstract-simple-course-service";

@Injectable({
    providedIn: 'root'
})
export class SubjectService extends AbstractSimpleCourseService<SubjectModel, { name: string[] }>
{
    public constructor(http: HttpClient) { super(http) }

    protected override get fetchAllValues(): Observable<SubjectModel[]> {
        return this.http.get<any[]>(`${this.BACKEND_URL}/course/subject/get/all`, { withCredentials: true });
    }

    protected createValue(models: { name: string[] }[]): Observable<SubjectModel[]> {
        return this.http.post<any[]>(`${this.BACKEND_URL}/course/subject/create`, models, { withCredentials: true });
    }

    protected override get translate(): OperatorFunction<any[], SubjectModel[]> {
        return map((response: any[]): SubjectModel[] =>
            response.map((item: any): SubjectModel => SubjectModel.fromObject(item))
        );
    }
}
