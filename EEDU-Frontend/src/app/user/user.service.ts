import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {LoginRequest} from "./login/login-request";

@Injectable({
    providedIn: 'root'
})
export class UserService {

    constructor(private http: HttpClient) {
    }

    login(data: LoginRequest) {
        this.http.post<string>("http://localhost:8080/user/login", data, {responseType: "text" as "json"}).subscribe(response => {

        })
    }
}
