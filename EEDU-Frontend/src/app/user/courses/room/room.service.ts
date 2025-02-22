import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable, OperatorFunction} from "rxjs";
import {RoomModel} from "./room-model";
import {AbstractCourseComponentsService} from "../abstract-course-components/abstract-course-components-service";
import {icons} from "../../../../environment/styles";

@Injectable({
    providedIn: 'root'
})
export class RoomService extends AbstractCourseComponentsService<string, RoomModel, { id: string }> {

    public constructor(http: HttpClient) { super(http, icons.room); }

    protected createValue(createModels: { id: string }[]): Observable<RoomModel[]>
    {
        return this.http.post<any[]>(`${this.BACKEND_URL}/course/room/create`, createModels, { withCredentials: true });
    }

    protected get fetchAllValues(): Observable<RoomModel[]>
    {
        return this.http.get<any[]>(`${this.BACKEND_URL}/course/room/get/all`, { withCredentials: true });
    }

    protected override deleteValue(id: string[]): Observable<void> {
        const url: string = `${this.BACKEND_URL}/course/room/delete/${id.toString()}`;
        return this.http.delete<void>(url, { withCredentials: true });
    }

    public override get translate(): OperatorFunction<any[], RoomModel[]>
    {
        return map((response: any[]): RoomModel[] =>
            response.map((item: any): RoomModel => RoomModel.fromObject(item))
        );
    }
}
