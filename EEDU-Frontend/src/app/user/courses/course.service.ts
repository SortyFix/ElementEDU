import {Injectable} from '@angular/core';
import {environment} from "../../../environment/environment";
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, map, Observable, tap} from "rxjs";
import {CourseModel} from "./models/course-model";
import {AppointmentCreateModel} from "./models/appointments/appointment-create-model";
import {AppointmentEntryModel} from "./models/appointments/appointment-entry-model";
import {FrequentAppointmentCreateModel} from "./models/appointments/frequent-appointment-create-model";
import {FrequentAppointmentModel} from "./models/appointments/frequent-appointment-model";

@Injectable({
    providedIn: 'root'
})
export class CourseService {

    private readonly BACKEND_URL: string = environment.backendUrl;
    private _fetched: boolean = false;
    private readonly _courseSubject: BehaviorSubject<CourseModel[]> = new BehaviorSubject<CourseModel[]>([]);

    constructor(
        private http: HttpClient,
    ) { }

    public fetchCourses(): Observable<CourseModel[]> {
        const url = `${this.BACKEND_URL}/course/get/courses/`;
        return this.http.get<any[]>(url, { withCredentials: true }).pipe(
            map((courses: any[]): CourseModel[] => {
                const courseModels: CourseModel[] = courses.map((course: any): CourseModel => CourseModel.fromObject(course));
                this._courseSubject.next(courseModels);
                this.fetched = true;
                return courseModels;
            })
        );
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
        const url = `${this.BACKEND_URL}/course/appointment/${course}/create`
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
        const url = `${this.BACKEND_URL}/course/appointment/${course}/schedule`;

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
            this.findCourseLazily(course)?.attachAppointment(appointment);
        }

        this.update();
    }

    private pushFrequent(course: number, objects: FrequentAppointmentModel[])
    {
        for (const appointment of objects) {
            this.findCourseLazily(course)?.attachFrequentAppointment(appointment);
        }

        this.update();
    }

    public get fetched(): boolean {
        return this._fetched;
    }

    private set fetched(value: boolean) {
        this._fetched = value;
    }

    public findCourseLazily(id: number): CourseModel | undefined {
        return this.findCourse(this.courses, id);
    }

    public findCourse(courses: CourseModel[], id: number): CourseModel | undefined {
        return courses.find((course: CourseModel): boolean => course.id === id);
    }

    public get courses(): CourseModel[]
    {
        return this._courseSubject.value;
    }

    public get courses$(): Observable<CourseModel[]> {
        return this._courseSubject.asObservable();
    }

    private update()
    {
        this._courseSubject.next([...this.courses]);
    }
}
