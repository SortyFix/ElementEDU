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

    login(data: LoginRequest)
    {
        this.loginRequest(data).subscribe({
            next: () => {
                console.log('Login successful');
                this.requestData().subscribe({
                    next: (data) => {
                        console.log('Data ', data);
                    },
                    error: (error) => {
                        console.error('Failed to retrieve data: :', error);
                    }
                })
            },
            error: (error) => {
                console.error('Login failed:', error);
            }
        });

    }

    private requestData(): Observable<any> {
        return this.http.get<any>("http://localhost:8080/user/get", { withCredentials: true });
    }

    private loginRequest(data: LoginRequest): Observable<void> {
        return this.http.post<void>("http://localhost:8080/user/login", data, {responseType: "text" as "json"});
    }
}
