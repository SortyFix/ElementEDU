import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, map, Observable, tap} from "rxjs";
import {RoomModel} from "./room-model";
import {environment} from "../../../../environment/environment";

@Injectable({
  providedIn: 'root'
})
export class RoomService {

    private readonly BACKEND_URL: string = environment.backendUrl;
    private readonly _roomSubject: BehaviorSubject<RoomModel[]> = new BehaviorSubject<RoomModel[]>([]);

    constructor(private http: HttpClient) { }

    public fetchRooms(): Observable<RoomModel[]> {
        const url = `${this.BACKEND_URL}/course/room/get/all`;
        return this.http.get<any[]>(url, { withCredentials: true }).pipe(
            map((response: any[]): RoomModel[] => response.map((item: any): RoomModel => RoomModel.fromObject(item))),
            tap((response: RoomModel[]): void => { this._roomSubject.next(response); }),
        );
    }

    public createRoom(room: { name: string }[]): Observable<RoomModel[]>
    {
        const url: string = `${this.BACKEND_URL}/course/room/create`;
        return this.http.post<any[]>(url, room, { withCredentials: true }).pipe(
            map((response: any[]): RoomModel[] =>
                response.map((item: any): RoomModel => RoomModel.fromObject(item))
            ),
            tap((response: RoomModel[]): void => { this._roomSubject.next([...this.rooms, ...response]); })
        );
    }

    public get rooms(): RoomModel[]
    {
        return this._roomSubject.value;
    }

    public get rooms$(): Observable<RoomModel[]>
    {
        if(this.rooms.length === 0)
        {
            this.fetchRooms().subscribe();
        }

        return this._roomSubject.asObservable();
    }
}
