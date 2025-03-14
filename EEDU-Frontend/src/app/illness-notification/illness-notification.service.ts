import { Injectable } from '@angular/core';
import {ReducedIllnessNotificationModel} from "./model/reduced-illness-notification-model";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environment/environment";
import {Observable, Subscription} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class IllnessNotificationService {
    protected backendUrl = environment.backendUrl;
    protected prefix: string = `${this.backendUrl}/illness/me`;

    constructor(public http: HttpClient) { }

    getOwnSickNotes(): Observable<ReducedIllnessNotificationModel[]> {
        return this.http.get<ReducedIllnessNotificationModel[]>(`${this.prefix}/my-notifications`, {
            withCredentials: true
        });
    }

    sendSickNoteRequest(formData: FormData): Subscription
    {
        return this.http.post(`${this.prefix}/excuse`, formData, {
            withCredentials: true
        }).subscribe(() => location.reload());
    }
}
