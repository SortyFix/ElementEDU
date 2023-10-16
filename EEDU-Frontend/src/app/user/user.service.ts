import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import { Router } from "@angular/router";
import {catchError, Observable} from "rxjs";
import {map} from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class UserService {
    constructor(private http: HttpClient, private router: Router) { }

    // NOT SECURE; PROTOTYPE!
    requestLogin(loginName: string, password: string): Observable<number> {
        const body: {loginName: string, password: string } = {loginName, password};
        const httpOptions = {
            headers: new HttpHeaders({'Content-Type':  'application/json'}),
            observe: 'response' as const
        };

        return this.http.post("http://localhost:8080/user/login", body, httpOptions).pipe(
            map(response => {
                this.router.navigate(["/home"]).then(r => console.log("Successful login; Switching to user dashboard."));
                console.log(response);
                console.log(response.status);
                return response.status;
            }),
            catchError(error => {
                console.error(error);
                console.log(error.status);
                throw error.status;
            })
        );
    }

}
