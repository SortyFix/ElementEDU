import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../../../../environment/environment";
import {GenericAssessmentCreateModel} from "./assessment-create-model";
import {BehaviorSubject, map, Observable, OperatorFunction} from "rxjs";
import {AssessmentModel, GenericAssessment} from "./assessment-model";

@Injectable({
  providedIn: 'root'
})
export class AssessmentService {

    private readonly _assessmentSubject: BehaviorSubject<readonly AssessmentModel[]> = new BehaviorSubject<readonly AssessmentModel[]>([]);

    public constructor(private readonly _http: HttpClient) {}

    protected get http(): HttpClient {
        return this._http;
    }

    protected get BACKEND_URL(): string
    {
        return `${environment.backendUrl}/course/appointment/assignment/assessment`;
    }

    public assess(obj: GenericAssessmentCreateModel[]): Observable<AssessmentModel[]>
    {
        const url: string = `${this.BACKEND_URL}/create`;
        return this.http.post<GenericAssessment[]>(url, obj, { withCredentials: true }).pipe(
            map((response: GenericAssessment[]): AssessmentModel[] =>
                response.map((item: GenericAssessment): AssessmentModel =>
                {
                    const assessment: AssessmentModel = AssessmentModel.fromObject(item)
                    this.pushAssessment(assessment);
                    return assessment;
                })
            )
        );
    }

    public updateFeedback(id: bigint, feedback: string): Observable<AssessmentModel>
    {
        const url: string = `${this.BACKEND_URL}/${id}/set/feedback/${feedback}`;
        return this.http.put<GenericAssessment>(url, { withCredentials: true }).pipe(this.translateAndPush);
    }

    public updateGrade(id: bigint, grade: number): Observable<AssessmentModel>
    {
        const url: string = `${this.BACKEND_URL}/${id}/set/grade/${grade}`;
        return this.http.put<GenericAssessment>(url, { withCredentials: true }).pipe(this.translateAndPush);
    }

    private get translateAndPush(): OperatorFunction<GenericAssessment, AssessmentModel>
    {
        return map((response: GenericAssessment): AssessmentModel =>
        {
            const model: AssessmentModel = AssessmentModel.fromObject(response);
            this.pushAssessment(model);
            return model;
        })
    }

    private pushAssessment(obj: AssessmentModel): void {
        let replaced: boolean = false;
        this.value.map((item: AssessmentModel): AssessmentModel => {
            if (item.id !== obj.id) {
                return item;
            }
            replaced = true;
            return obj;
        });

        if (replaced) {
            return;
        }

        this._assessmentSubject.next([...this.value, obj]);
    }

    public get value(): readonly AssessmentModel[]
    {
        return this._assessmentSubject.value;
    }

    public get values$(): Observable<readonly AssessmentModel[]> {
        return this._assessmentSubject.asObservable();
    }
}
