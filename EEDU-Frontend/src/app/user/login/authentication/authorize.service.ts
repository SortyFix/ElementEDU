import {Injectable} from '@angular/core';
import {map, Observable, tap} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {LoginRequest} from "./login-name-form/login-request";
import {UserService} from "../../user.service";

@Injectable({
    providedIn: 'root'
})
export class AuthorizeService
{
    token?: string;

    constructor(private userService: UserService, private http: HttpClient)
    {
    }

    request(data: LoginRequest): Observable<void>
    {
        const url = "http://localhost:8080/user/login";
        return this.http.post<string>(url, data, {responseType: "text" as "json"}).pipe(tap<string>({
            next: value => this.token = value
        }), map(() => {}));
    }

    verifyPassword(password: string): Observable<string>
    {
        const url = "http://localhost:8080/user/login/credentials/verify";
        return this.http.post<string>(url, password, {
            responseType: "text" as "json", headers: {"Authorization": "Bearer " + this.token}, withCredentials: true
        }).pipe(tap<string>({next: () => this.userService.loadData()}));
    }
}
