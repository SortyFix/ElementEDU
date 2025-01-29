import {Injectable} from '@angular/core';
import {AbstractSimpleCourseService} from "../abstract-simple-course-service";
import {ClassRoomModel} from "./class-room-model";
import {map, Observable, OperatorFunction} from 'rxjs';
import {ClassRoomCreateModel, ClassRoomCreatePacket} from "./class-room-create-model";

@Injectable({
    providedIn: 'root'
})
export class ClassRoomService extends AbstractSimpleCourseService<ClassRoomModel, ClassRoomCreateModel> {

    protected override get fetchAllValues(): Observable<ClassRoomModel[]> {
        return this.http.get<any[]>(`${this.BACKEND_URL}/course/classroom/get/all`, {withCredentials: true});
    }

    protected override createValue(createModels: ClassRoomCreateModel[]): Observable<ClassRoomModel[]> {
        const url: string = `${this.BACKEND_URL}/course/classroom/create`;
        return this.http.post<any[]>(url, this.toPackets(createModels), {withCredentials: true});
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
