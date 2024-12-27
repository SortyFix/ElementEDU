import {Injectable} from '@angular/core';
import {environment} from "../../../environment/environment";
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, map, Observable, tap} from "rxjs";
import {CourseModel} from "./models/course-model";
import {AppointmentCreateModel} from "./models/appointments/appointment-create-model";
import {AppointmentEntryModel} from "./models/appointments/appointment-entry-model";

@Injectable({
    providedIn: 'root'
})
export class CourseService {
    private readonly BACKEND_URL: string = environment.backendUrl;
    private readonly _courseSubject: BehaviorSubject<CourseModel[]> = new BehaviorSubject<CourseModel[]>([]);

    constructor(private http: HttpClient) { }

    public fetchCourses(): Observable<CourseModel[]> {
        const url = `${this.BACKEND_URL}/course/get/courses/`;
        return this.http.get<any[]>(url, { withCredentials: true }).pipe(
            map((courses: any[]): CourseModel[] => {
                const courseModels: CourseModel[] = courses.map((course: any): CourseModel => CourseModel.fromObject(course));
                this._courseSubject.next(courseModels);
                return courseModels;
            })
        );
    }

    public createAppointment(course: number, appointment: AppointmentCreateModel)
    {
        const url = `${this.BACKEND_URL}/course/${course}/appointment/set`
        return this.http.post<any>(url, {

            start: appointment.start,
            duration: appointment.duration,
            description: appointment.description,
            assignment: appointment.assignment,

        }, { withCredentials: true }).pipe(tap({
            next: (appointment: any): void => {
                const courses: CourseModel[] = this._courseSubject.value;
                const targetCourse: CourseModel | undefined = courses.find((c: CourseModel): boolean => c.id === course);

                if (targetCourse) {
                    targetCourse.addAppointment(AppointmentEntryModel.fromObject(appointment));
                    this._courseSubject.next([...courses]);
                }
            }
        }));
    }

    public get courses$(): Observable<CourseModel[]> {
        return this._courseSubject.asObservable();
    }
}
