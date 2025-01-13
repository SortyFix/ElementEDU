import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../environment/environment";
import {CourseService} from "../course.service";
import {map, Observable, tap} from "rxjs";
import {AppointmentCreateModel} from "./entry/appointment-create-model";
import {AppointmentEntryModel} from "./entry/appointment-entry-model";
import {FrequentAppointmentCreateModel} from "./frequent/frequent-appointment-create-model";
import {FrequentAppointmentModel} from "./frequent/frequent-appointment-model";
import {AppointmentUpdateModel} from "./entry/appointment-update-model";

@Injectable({
    providedIn: 'root'
})
export class AppointmentService {

    private readonly BACKEND_URL: string = environment.backendUrl;

    constructor(private readonly _http: HttpClient, private readonly _courseService: CourseService) { }

    protected get http(): HttpClient {
        return this._http;
    }

    protected get courseService(): CourseService {
        return this._courseService;
    }

    /**
     * Creates appointments for a specified course.
     *
     * This method sends an HTTP POST request to create new appointments for a given course based on the provided
     * {@link AppointmentCreateModel} data. The appointments are converted into packets before being sent to the backend.
     * The response is mapped into an array of {@link AppointmentEntryModel} instances.
     *
     * @param course the id of the course for which appointments are being created.
     * @param createModel an array of {@link AppointmentCreateModel} instances containing the appointment details.
     * @returns an observable that emits an array of {@link AppointmentEntryModel} instances created by the backend.
     * @public
     */
    public createAppointment(course: number, createModel: AppointmentCreateModel[]): Observable<AppointmentEntryModel[]>
    {
        const url = `${this.BACKEND_URL}/course/appointment/${course}/schedule/standalone`
        return this.http.post<any[]>(url, createModel.map((current: AppointmentCreateModel):
        {
            start: number,
            duration: number,
            description?: string,
            assignment?: AppointmentCreateModel
        } => current.toPacket), { withCredentials: true }).pipe(
            map((response: any[]): AppointmentEntryModel[] =>
                response.map((item: any): AppointmentEntryModel => AppointmentEntryModel.fromObject(item))
            ),
            tap({ next: (response: AppointmentEntryModel[]): void => this.pushAppointment(course, response)})
        );
    }

    public updateAppointment(appointment: number, updateModel: AppointmentUpdateModel): Observable<AppointmentEntryModel>
    {
        const url = `${this.BACKEND_URL}/course/appointment/update/standalone/${appointment}`
        return this.http.post<any>(url, updateModel.toPacket, { withCredentials: true }).pipe(
            map((response: any): AppointmentEntryModel => AppointmentEntryModel.fromObject(response))
        );
    }

    /**
     * Creates recurring appointments for a specified course
     *
     * This method sends an HTTP POST request to schedule recurring appointments for a given course using the
     * provided {@link FrequentAppointmentCreateModel} data. The recurring appointments are converted into packets
     * before being sent to the backend. The response is mapped into an array of {@link FrequentAppointmentModel} instances.
     *
     * @param course the id of the course for which recurring appointments are being scheduled.
     * @param createModel an array of {@link FrequentAppointmentCreateModel} instances containing the appointment details.
     * @returns an observable that emits an array of {@link FrequentAppointmentModel} instances created by the backend.
     * @public
     */
    public createFrequent(course: number, createModel: FrequentAppointmentCreateModel[]): Observable<FrequentAppointmentModel[]> {
        const url = `${this.BACKEND_URL}/course/appointment/${course}/schedule/frequent`;

        return this.http.post<any[]>(url, createModel.map((current: FrequentAppointmentCreateModel):
        {
            start: number;
            until: number;
            room: number;
            duration: number;
            frequency: number
        } => current.toPacket), { withCredentials: true }).pipe(
            map((response: any[]): FrequentAppointmentModel[] =>
                response.map((item: any): FrequentAppointmentModel => FrequentAppointmentModel.fromObject(item, []))
            ),
            tap({ next: (appointments: FrequentAppointmentModel[]): void => this.pushFrequent(course, appointments) })
        );
    }

    private pushAppointment(course: number, objects: AppointmentEntryModel[])
    {
        for(const appointment of objects) {
            this.courseService.findCourseLazily(course)?.attachAppointment(appointment);
        }

        this.courseService.update();
    }

    private pushFrequent(course: number, objects: FrequentAppointmentModel[])
    {
        for (const appointment of objects) {
            this.courseService.findCourseLazily(course)?.attachFrequentAppointment(appointment);
        }

        this.courseService.update();
    }
}
