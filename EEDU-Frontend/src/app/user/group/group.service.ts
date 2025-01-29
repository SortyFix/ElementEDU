import {Injectable} from '@angular/core';
import {environment} from "../../../environment/environment";
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {GroupModel} from "../group-model";

@Injectable({
    providedIn: 'root'
})
export class GroupService {

    private readonly BACKEND_URL: string = environment.backendUrl;


    constructor(private http: HttpClient) {}

    public fetchAll(): Observable<GroupModel[]> {
        const url: string = `${this.BACKEND_URL}/user/group/get/all`;
        return this.http.get<any[]>(url, {withCredentials: true}).pipe(map((response: any[]): GroupModel[] => {
            return response.map((element: any): GroupModel => GroupModel.fromObject(element))
        }));
    }
}
