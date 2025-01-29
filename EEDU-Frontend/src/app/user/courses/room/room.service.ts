import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {RoomModel} from "./room-model";
import {AbstractSimpleCourseService} from "../abstract-simple-course-service";

@Injectable({
  providedIn: 'root'
})
export class RoomService extends AbstractSimpleCourseService<RoomModel, { name: string[] }>{

    constructor(http: HttpClient) { super(http); }

    protected createValue(createModels: { name: string[] }[]): Observable<RoomModel[]> {
        const url: string = `${this.BACKEND_URL}/course/room/create`;
        return this.http.post<any[]>(url, createModels, { withCredentials: true }).pipe(
            map((response: any[]): RoomModel[] =>
                response.map((item: any): RoomModel => RoomModel.fromObject(item))
            )
        );
    }

    protected get fetchAllValues(): Observable<RoomModel[]> {
        const url = `${this.BACKEND_URL}/course/room/get/all`;
        return this.http.get<any[]>(url, { withCredentials: true }).pipe(
            map((response: any[]): RoomModel[] => response.map((item: any): RoomModel => RoomModel.fromObject(item))),
        );
    }
}
