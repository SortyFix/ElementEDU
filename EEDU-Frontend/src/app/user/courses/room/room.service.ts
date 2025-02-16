import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable, OperatorFunction} from "rxjs";
import {RoomModel} from "./room-model";
import {AbstractCourseComponentsService} from "../abstract-course-components-service";

@Injectable({
    providedIn: 'root'
})
export class RoomService extends AbstractCourseComponentsService<RoomModel, { name: string }> {

    constructor(http: HttpClient) { super(http); }

    protected createValue(createModels: { name: string }[]): Observable<RoomModel[]>
    {
        return this.http.post<any[]>(`${this.BACKEND_URL}/course/room/create`, createModels, { withCredentials: true });
    }

    protected get fetchAllValues(): Observable<RoomModel[]>
    {
        return this.http.get<any[]>(`${this.BACKEND_URL}/course/room/get/all`, { withCredentials: true });
    }

    protected override get translate(): OperatorFunction<any[], RoomModel[]>
    {
        return map((response: any[]): RoomModel[] =>
            response.map((item: any): RoomModel => RoomModel.fromObject(item))
        );
    }
}
