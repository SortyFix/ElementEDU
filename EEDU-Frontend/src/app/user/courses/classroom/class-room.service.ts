import {Injectable} from '@angular/core';
import {ClassRoomModel, GenericClassRoom} from "./class-room-model";
import {Observable, OperatorFunction} from 'rxjs';
import {ClassRoomCreateModel} from "./class-room-create-model";
import {HttpClient} from "@angular/common/http";
import {CourseModel, GenericCourse} from "../course-model";
import {CourseService} from "../course.service";
import {EntityService} from "../../../entity/entity-service";
import {CreateClassRoomDialogComponent} from "./create-class-room-dialog/create-class-room-dialog.component";
import {AbstractSimpleCreateEntity} from "../../../entity/create-entity/abstract-simple-create-entity";

@Injectable({
    providedIn: 'root'
})
export class ClassRoomService extends EntityService<string, ClassRoomModel, GenericClassRoom, ClassRoomCreateModel> {
    private readonly _translateCourses: OperatorFunction<GenericCourse[], CourseModel[]>;

    public constructor(http: HttpClient, courseService: CourseService) {
        super(http, 'course/classroom', CreateClassRoomDialogComponent as unknown as typeof AbstractSimpleCreateEntity);
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
