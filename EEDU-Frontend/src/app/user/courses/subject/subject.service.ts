import { Injectable } from '@angular/core';
import {BehaviorSubject, map, Observable, tap} from "rxjs";
import {SubjectModel} from "./subject-model";
import {environment} from "../../../../environment/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class SubjectService {

    private readonly BACKEND_URL: string = environment.backendUrl;
    private readonly _subjectSubject: BehaviorSubject<SubjectModel[]> = new BehaviorSubject<SubjectModel[]>([]);

    constructor(private readonly _http: HttpClient,) { }

    public fetchSubjects(): Observable<SubjectModel[]> {
        const url: string = `${this.BACKEND_URL}/course/subject/get/all`;
        return this.http.get<any[]>(url, { withCredentials: true }).pipe(
            map((subject: any[]): SubjectModel[] =>
                subject.map((item: any): SubjectModel => SubjectModel.fromObject(item))
            ),
            tap((subject: SubjectModel[]): void => { this._subjectSubject.next(subject); }),
        );
    }

    public createSubject(subject: { name: string }[]): Observable<SubjectModel[]>
    {
        const url: string = `${this.BACKEND_URL}/course/subject/create`;
        return this.http.post<any[]>(url, subject, { withCredentials: true }).pipe(
            map((response: any[]): SubjectModel[] =>
                response.map((item: any): SubjectModel => SubjectModel.fromObject(item))
            ),
            tap((response: SubjectModel[]): void => { this._subjectSubject.next([...this.subjects, ...response]); })
        );
    }

    public get subjects(): SubjectModel[]
    {
        return this._subjectSubject.value;
    }

    public get subjects$(): Observable<SubjectModel[]> {
        return this._subjectSubject.asObservable();
    }

    protected get http(): HttpClient {
        return this._http;
    }
}
