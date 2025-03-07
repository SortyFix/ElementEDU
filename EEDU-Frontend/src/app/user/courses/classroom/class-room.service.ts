import {Injectable} from '@angular/core';
import {ClassRoomModel, GenericClassRoom} from "./class-room-model";
import {Observable, OperatorFunction} from 'rxjs';
import {ClassRoomCreateModel} from "./class-room-create-model";
import {HttpClient} from "@angular/common/http";
import {CourseModel, GenericCourse} from "../course-model";
import {CourseService} from "../course.service";
import {EntityService} from "../../../entity/entity-service";

@Injectable({
    providedIn: 'root'
})
export class ClassRoomService extends EntityService<string, ClassRoomModel, GenericClassRoom, ClassRoomCreateModel> {
    private readonly _translateCourses: OperatorFunction<GenericCourse[], CourseModel[]>;

    public constructor(http: HttpClient, courseService: CourseService) {
        super(http, 'course/classroom');
        this._translateCourses = courseService.translateValue;
    }

    public override translate(obj: GenericClassRoom): ClassRoomModel {
        return ClassRoomModel.fromObject(obj);
    }

    public fetchCourses(classRoom: string): Observable<CourseModel[]> {
        const url: string = `${this.BACKEND_URL}/course/classroom/get/courses/${classRoom}`;
        return this.http.get<GenericCourse[]>(url, {withCredentials: true}).pipe(this._translateCourses);
    }
}
