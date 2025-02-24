import {Injectable} from '@angular/core';
import {ClassRoomModel} from "./class-room-model";
import {map, Observable, OperatorFunction} from 'rxjs';
import {ClassRoomCreateModel, ClassRoomCreatePacket} from "./class-room-create-model";
import {HttpClient} from "@angular/common/http";
import {AbstractCourseComponentsService} from "../abstract-course-components/abstract-course-components-service";
import {icons} from "../../../../environment/styles";
import {CourseModel} from "../course-model";
import {CourseService} from "../course.service";

@Injectable({
    providedIn: 'root'
})
export class ClassRoomService extends AbstractCourseComponentsService<string, ClassRoomModel, ClassRoomCreateModel> {

    private readonly _translateCourses: OperatorFunction<any[], CourseModel[]>;

    public constructor(http: HttpClient, courseService: CourseService) {
        super(http, icons.classroom);
        this._translateCourses = courseService.translate;
    }

    public override get translate(): OperatorFunction<any[], ClassRoomModel[]> {
        return map((response: any[]): ClassRoomModel[] => response.map((item: any): ClassRoomModel =>
            ClassRoomModel.fromObject(item))
        );
    }

    protected override get fetchAllValues(): Observable<any[]> {
        return this.http.get<any[]>(`${this.BACKEND_URL}/course/classroom/get/all`, {withCredentials: true});
    }

    public fetchCourses(classRoom: string): Observable<CourseModel[]> {
        const url: string = `${this.BACKEND_URL}/course/classroom/get/courses/${classRoom}`;
        return this.http.get<any[]>(url, {withCredentials: true}).pipe(this._translateCourses);
    }

    protected override createValue(createModels: ClassRoomCreateModel[]): Observable<any[]> {
        const url: string = `${this.BACKEND_URL}/course/classroom/create`;
        return this.http.post<any[]>(url, this.toPackets(createModels), {withCredentials: true});
    }

    protected override deleteValue(id: string[]): Observable<void> {
        const url: string = `${this.BACKEND_URL}/course/classroom/delete/${id.toString()}`;
        return this.http.delete<void>(url, {withCredentials: true});
    }

    private toPackets(createModels: ClassRoomCreateModel[]): ClassRoomCreatePacket[] {
        return createModels.map((createModels: ClassRoomCreateModel): ClassRoomCreatePacket => createModels.toPacket);
    }
}
