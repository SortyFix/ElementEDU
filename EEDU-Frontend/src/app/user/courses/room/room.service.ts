import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, map, Observable} from "rxjs";
import {RoomModel} from "./room-model";
import {environment} from "../../../../environment/environment";

@Injectable({
  providedIn: 'root'
})
export class RoomService {

    private readonly BACKEND_URL: string = environment.backendUrl;
    private _fetched: boolean = false;
    private readonly _roomSubject: BehaviorSubject<RoomModel[]> = new BehaviorSubject<RoomModel[]>([]);

    constructor(
        private http: HttpClient,
    ) { }

    public fetchRooms(): Observable<RoomModel[]> {
        const url = `${this.BACKEND_URL}/course/room/all`;
        return this.http.get<any[]>(url, { withCredentials: true }).pipe(
            map((roomArray: any[]): RoomModel[] => {
                const rooms: RoomModel[] = roomArray.map((room: any): RoomModel => RoomModel.fromObject(room));
                this._roomSubject.next(rooms);
                this._fetched = true;
                return rooms;
            })
        );
    }

    public get rooms(): RoomModel[]
    {
        return this._roomSubject.value;
    }

    public get rooms$(): Observable<RoomModel[]>
    {
        return this._roomSubject.asObservable();
    }
}
