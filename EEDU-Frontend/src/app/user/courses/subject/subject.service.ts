import { Injectable } from '@angular/core';
import {map, Observable, tap} from "rxjs";
import {SubjectModel} from "./subject-model";
import {HttpClient} from "@angular/common/http";
import {AbstractSimpleCourseService} from "../abstract-simple-course-service";

@Injectable({
  providedIn: 'root'
})
export class SubjectService extends AbstractSimpleCourseService<SubjectModel, { name: string[] }> {

    constructor(http: HttpClient) { super(http) }

    protected override get fetchAllValues(): Observable<SubjectModel[]> {
        const url: string = `${this.BACKEND_URL}/course/subject/get/all`;
        return this.http.get<any[]>(url, { withCredentials: true }).pipe(
            map((subject: any[]): SubjectModel[] =>
                subject.map((item: any): SubjectModel => SubjectModel.fromObject(item))
            )
        );
    }

    protected createValue(createModels: { name: string[] }[]): Observable<SubjectModel[]> {
        const url: string = `${this.BACKEND_URL}/course/subject/create`;
        return this.http.post<any[]>(url, createModels, { withCredentials: true }).pipe(
            map((response: any[]): SubjectModel[] =>
                response.map((item: any): SubjectModel => SubjectModel.fromObject(item))
            )
        );
    }
}
