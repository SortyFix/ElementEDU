import {Injectable} from '@angular/core';
import {ClassRoomModel} from "./class-room-model";
import {map, Observable, OperatorFunction} from 'rxjs';
import {ClassRoomCreateModel, ClassRoomCreatePacket} from "./class-room-create-model";
import {HttpClient} from "@angular/common/http";
import {AbstractCourseComponentsService} from "../abstract-course-components/abstract-course-components-service";

@Injectable({
    providedIn: 'root'
})
export class ClassRoomService extends AbstractCourseComponentsService<ClassRoomModel, ClassRoomCreateModel> {

    constructor(http: HttpClient) { super(http); }

    protected override get fetchAllValues(): Observable<ClassRoomModel[]> {
        return this.http.get<any[]>(`${this.BACKEND_URL}/course/classroom/get/all`, {withCredentials: true});
    }

    protected override createValue(createModels: ClassRoomCreateModel[]): Observable<any[]> {
        const url: string = `${this.BACKEND_URL}/course/classroom/create`;
        console.log(this.toPackets(createModels));
        return this.http.post<any[]>(url, this.toPackets(createModels), { withCredentials: true });
    }

    protected override get translate(): OperatorFunction<any[], ClassRoomModel[]>
    {
        return map((response: any[]): ClassRoomModel[] => response.map((item: any): ClassRoomModel =>
            ClassRoomModel.fromObject(item))
        );
    }

    private toPackets(createModels: ClassRoomCreateModel[]): ClassRoomCreatePacket[]
    {
        return createModels.map((createModels: ClassRoomCreateModel): ClassRoomCreatePacket => createModels.toPacket);
    }
}
