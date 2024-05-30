import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {LoginRequest} from "./login/login-request";
import {Observable} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class UserService {

    constructor(private http: HttpClient) {
    }

    login(data: LoginRequest): Observable<void>
    {
        return this.http.post<void>("http://localhost:8080/user/login", data, {
            responseType: "text" as "json",
            withCredentials: true
        });
    }

    private getUserdata(): Observable<any> {
        return this.http.get<any>("http://localhost:8080/user/get", { withCredentials: true });
    }
}
