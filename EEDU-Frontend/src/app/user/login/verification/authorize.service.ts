import {Injectable} from '@angular/core';
import {map, Observable, tap} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {LoginRequest} from "./login-request";

@Injectable({
    providedIn: 'root'
})
export class AuthorizeService {

    token?: string;

    constructor(private http: HttpClient) {
    }

    request(data: LoginRequest): Observable<void> {
        const url = "http://localhost:8080/user/login";
        return this.http.post<string>(url, data, {responseType: "text" as "json"}).pipe(tap<string>({
            next: value => {
                this.token = value;
            }
        }), map(() => {
            return;
        }));
    }
}
