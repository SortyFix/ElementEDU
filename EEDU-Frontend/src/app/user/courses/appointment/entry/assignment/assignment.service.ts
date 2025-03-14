import {Injectable} from '@angular/core';
import {HttpClient, HttpEvent} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {AssignmentInsightModel, GenericAssignmentInsightModel} from "./assignment-insight-model";
import {FileService} from "../../../../../file/file.service";
import {AssignmentModel} from "./assignment-model";
import {AppointmentService} from "../../appointment.service";
import {AppointmentEntryModel} from "../appointment-entry-model";
import {environment} from "../../../../../../environment/environment";

@Injectable({
    providedIn: 'root'
})
export class AssignmentService {

    public constructor(private readonly _http: HttpClient,
        private readonly _fileService: FileService,
        private readonly _appointmentService: AppointmentService) {
    }

    public downloadAssignment(appointment: bigint, user: bigint, file: string): void {
        //TODO
    }

    public get nextAssignments(): readonly AssignmentModel[] {
        return this._appointmentService.nextAppointments.filter(
            (appointment: AppointmentEntryModel): boolean => !!appointment.assignment
        ).map((appointment: AppointmentEntryModel): AssignmentModel => <AssignmentModel>appointment.assignment);
    }

    public fetchInsights(appointment: bigint): Observable<AssignmentInsightModel[]> {
        const url: string = `${this.BACKEND_URL}/${appointment}/status/all`;
        return this.http.get<GenericAssignmentInsightModel[]>(url, {withCredentials: true}).pipe(
            map((response: GenericAssignmentInsightModel[]): AssignmentInsightModel[] =>
                response.map((item: GenericAssignmentInsightModel): AssignmentInsightModel => AssignmentInsightModel.fromObject(item))
            )
        );
    }

    public fetchInsight(appointment: bigint): Observable<AssignmentInsightModel> {
        const url: string = `${this.BACKEND_URL}/${appointment}/status`;
        return this.http.get<GenericAssignmentInsightModel>(url, {withCredentials: true}).pipe(map((response: GenericAssignmentInsightModel): AssignmentInsightModel => AssignmentInsightModel.fromObject(response)));
    }

    public fetchUsersInsight(appointment: bigint, user: bigint): Observable<AssignmentInsightModel> {
        const url: string = `${this.BACKEND_URL}/${appointment}/status/${user}`;
        return this.http.get<GenericAssignmentInsightModel>(url, {withCredentials: true}).pipe(map((response: GenericAssignmentInsightModel): AssignmentInsightModel => AssignmentInsightModel.fromObject(response)));
    }

    public submitAssignment(appointment: bigint, assignmentFiles: File[]): Observable<HttpEvent<any>> {
        const url: string = `${this.BACKEND_URL}/${appointment}/submit`;
        return this._fileService.uploadFiles(url, assignmentFiles);
    }

    public deleteAssignment(appointment: bigint, assignmentFiles: string[]): Observable<void> {
        const url: string = `${this.BACKEND_URL}/${appointment}/delete/${assignmentFiles.toString()}`;
        return this.http.delete<void>(url, {withCredentials: true});
    }

    private get BACKEND_URL(): string
    {
        return `${environment.backendUrl}/course/appointment/assignment`;
    }

    private get http(): HttpClient {
        return this._http;
    }
}
