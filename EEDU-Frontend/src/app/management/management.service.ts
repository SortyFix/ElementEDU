import { Injectable } from '@angular/core';
import {UserService} from "../user/user.service";
import {FileService} from "../file/file.service";
import {HttpClient} from "@angular/common/http";
import {
    GenericIllnessNotificationModel,
    IllnessNotificationModel
} from "../illness-notification/model/illness-notification-model";
import {environment} from "../../environment/environment";
import {map, Observable} from "rxjs";
import {IllnessNotificationStatus} from "../illness-notification/illness-notification-status";

@Injectable({
    providedIn: 'root'
})
export class ManagementService {
    private readonly BACKEND_URL: string = environment.backendUrl;

    constructor(protected userService: UserService, protected fileService: FileService, private http: HttpClient) {
    }

    getPendingNotifications(): Observable<IllnessNotificationModel[]>
    {
        return this.http.get<GenericIllnessNotificationModel[]>(`${this.BACKEND_URL}/illness/management/get-pending`, {
            withCredentials: true
        }).pipe(
            map((list: GenericIllnessNotificationModel[]): IllnessNotificationModel[] =>
                list.map((obj: GenericIllnessNotificationModel) =>
                    IllnessNotificationModel.fromObject(obj)
                )
            )
        );
    }

    respondToNotification(id: bigint, status: IllnessNotificationStatus): Observable<boolean> {
        return this.http.put<boolean>(`${this.BACKEND_URL}/illness/management/respond/${id}`, JSON.stringify(status), {
            headers: { 'Content-Type': 'application/json' },
            withCredentials: true
        });
    }

}
