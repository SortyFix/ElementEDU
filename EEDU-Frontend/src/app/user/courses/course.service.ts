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

    public createAppointment(course: number, appointment: AppointmentCreateModel)
    {
        const url = `${this.BACKEND_URL}/course/appointment/${course}/create`
        return this.http.post<any[]>(url, [appointment.toPacket], {withCredentials: true}).pipe(tap({
            next: (response: any[]): void => this.pushAppointment(course, response)
        }));
    }

    public createFrequent(course: number, appointment: FrequentAppointmentCreateModel) {
        const url = `${this.BACKEND_URL}/course/appointment/${course}/schedule`
        return this.http.post<any[]>(url, [appointment.toPacket], {withCredentials: true}).pipe(tap({
            next: (response: any[]): void => this.pushFrequent(course, response)
        }));
    }

    private pushAppointment(course: number, objects: any[])
    {
        for(const appointment of objects) {
            this.findCourseLazily(course)?.attachAppointment(AppointmentEntryModel.fromObject(appointment));
        }

        this.update();
    }

    private pushFrequent(course: number, objects: any[])
    {
        for (const appointment of objects) {
            const frequentModel: FrequentAppointmentModel = FrequentAppointmentModel.fromObject(appointment, []);
            this.findCourseLazily(course)?.attachFrequentAppointment(frequentModel);
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
