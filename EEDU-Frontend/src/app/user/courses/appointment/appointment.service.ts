import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../environment/environment";
import {CourseService} from "../course.service";
import {map, Observable, tap} from "rxjs";
import {AppointmentCreateModel} from "./entry/appointment-create-model";
import {AppointmentEntryModel} from "./entry/appointment-entry-model";
import {
    FrequentAppointmentCreateModel, FrequentAppointmentCreatePacket
} from "./frequent/frequent-appointment-create-model";
import {FrequentAppointmentModel} from "./frequent/frequent-appointment-model";
import {AppointmentUpdateModel} from "./entry/appointment-update-model";
import {CourseModel} from "../course-model";
import {AssignmentCreateModel} from "./entry/assignment/assignment-create-model";

@Injectable({
    providedIn: 'root'
})
export class AppointmentService {

    private readonly BACKEND_URL: string = `${environment.backendUrl}/course/appointment`;

    constructor(private readonly _http: HttpClient, private readonly _courseService: CourseService) { }

    public get nextAppointments(): Observable<readonly AppointmentEntryModel[]> {
        const currentDate: Date = new Date();
        return this.courseService.ownCourses$.pipe(map((courses: CourseModel[]): AppointmentEntryModel[] => {
            return courses.flatMap((course: CourseModel): readonly AppointmentEntryModel[] => course.appointmentEntries).filter((appointment: AppointmentEntryModel): boolean => appointment.start > currentDate).sort((a: AppointmentEntryModel, b: AppointmentEntryModel): number => a.start.getTime() - b.start.getTime())
        }));
    }

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
    public createAppointment(course: bigint, createModel: AppointmentCreateModel[]): Observable<AppointmentEntryModel[]> {
        const url = `${this.BACKEND_URL}/${course}/schedule/standalone`
        return this.http.post<any[]>(url, createModel.map((current: AppointmentCreateModel): {
            start: number, duration: number, description?: string, assignment?: AppointmentCreateModel
        } => current.toPacket), {withCredentials: true}).pipe(map((response: any[]): AppointmentEntryModel[] => response.map((item: any): AppointmentEntryModel => AppointmentEntryModel.fromObject(item))), tap({next: (response: AppointmentEntryModel[]): void => this.pushAppointment(response)}));
    }

    public setDescription(appointment: bigint, description?: string | null): Observable<void>
    {
        if(!description)
        {
            return this.unsetDescription(appointment);
        }

        const url = `${this.BACKEND_URL}/update/standalone/${appointment}/set/description/${description}`
        return this.http.put<void>(url, null, { withCredentials: true });
    }

    public unsetDescription(appointment: bigint): Observable<void>
    {
        const url = `${this.BACKEND_URL}/update/standalone/${appointment}/unset/description`
        return this.http.delete<void>(url, { withCredentials: true });
    }

    public setRoom(appointment: bigint, room?: string | null): Observable<void>
    {
        if(!room)
        {
            return this.unsetRoom(appointment);
        }

        const url = `${this.BACKEND_URL}/update/standalone/${appointment}/set/room/${room}`
        return this.http.put<void>(url, null, { withCredentials: true });
    }

    public unsetRoom(appointment: bigint): Observable<void>
    {
        const url = `${this.BACKEND_URL}/update/standalone/${appointment}/unset/room`
        return this.http.delete<void>(url, { withCredentials: true });
    }

    public setAssignment(appointment: bigint, assignment?: AssignmentCreateModel | null): Observable<void>
    {
        if(!assignment)
        {
            return this.unsetAssignment(appointment);
        }

        const url = `${this.BACKEND_URL}/update/standalone/${appointment}/set/assignment`
        return this.http.put<void>(url, assignment.toPacket(), { withCredentials: true });
    }

    public unsetAssignment(appointment: bigint): Observable<void>
    {
        const url = `${this.BACKEND_URL}/update/standalone/${appointment}/unset/assignment`
        return this.http.delete<void>(url, { withCredentials: true });
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
    public createFrequent(course: bigint, createModel: FrequentAppointmentCreateModel[]): Observable<FrequentAppointmentModel[]> {
        const url = `${this.BACKEND_URL}/${course}/schedule/frequent`;

        return this.http.post<any[]>(url, createModel.map((current: FrequentAppointmentCreateModel): FrequentAppointmentCreatePacket => current.toPacket), {withCredentials: true}).pipe(map((response: any[]): FrequentAppointmentModel[] => {
            return response.map((item: any): FrequentAppointmentModel => FrequentAppointmentModel.fromObject(item, (): CourseModel => {
                return <CourseModel>this.courseService.findCourseLazily(course);
            }))
        }), tap({
            next: (appointments: FrequentAppointmentModel[]): void => {
                this.pushFrequent(course, appointments)
            }
        }));
    }

    private pushAppointment(objects: AppointmentEntryModel[]): void {
        for (const appointment of objects) {
            this.courseService.findCourseLazily(appointment.course)?.attachAppointment(appointment);
        }

        this.courseService.update();
    }

    private pushFrequent(course: bigint, objects: FrequentAppointmentModel[]) {
        for (const appointment of objects) {
            this.courseService.findCourseLazily(course)?.attachFrequentAppointment(appointment);
        }

        this.courseService.update();
    }
}
