import {Injectable} from '@angular/core';
import {map, Observable, tap} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {LoginRequest} from "./auth-modal/request/login-request";

@Injectable({
    providedIn: 'root'
})
export class AuthorizeService
{
    token?: string;

    constructor(private http: HttpClient)
    {
    }

    async isAuthorized(): Promise<boolean>
    {
        if (localStorage.getItem("authorized"))
        {
            return localStorage.getItem("authorized") == "true";
        }
        const url = "http://localhost:8080/user/authorized";
        const response = await fetch(url, {
            credentials: "include"
        })
        localStorage.setItem("authorized", String(response.ok))
        return response.ok;
    }

    request(data: LoginRequest): Observable<void>
    {
        const url = "http://localhost:8080/user/login";
        return this.http.post<string>(url, data, {responseType: "text" as "json"}).pipe(tap<string>({
            next: value =>
            {
                this.token = value;
            }, error: (error) => console.log(error)
        }), map(() =>
        {
            return;
        }));
    }

    verifyPassword(password: string): Observable<string>
    {
        const url = "http://localhost:8080/user/login/credentials/verify";
        return this.http.post<string>(url, password, {
            responseType: "text" as "json", headers: {"Authorization": "Bearer " + this.token}, withCredentials: true
        }).pipe(tap<string>({
            next: () =>
            {
                localStorage.setItem("authorized", "true")
            }
        }));
    }
}
