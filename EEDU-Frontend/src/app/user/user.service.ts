import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import { Router } from "@angular/router";

@Injectable({
    providedIn: 'root'
})
export class UserService {
    constructor(private http: HttpClient, private router: Router) { }

    // NOT SECURE; PROTOTYPE!
    requestLogin(loginName: string, password: string){
        const body: {loginName: string, password: string } = {loginName, password};
        const httpOptions = {
            headers: new HttpHeaders({'Content-Type':  'application/json'}),
            observe: 'response' as const
        };

        this.http.post("http://localhost:8080/user/login", body, httpOptions)
            .subscribe({
                next: response => {
                    if(response.status === 200){
                        // Temporary navigation to default home page, TODO user specific pages
                        this.router.navigate(["/home"]).then(r => console.log("Successful login; Switching to user dashboard."));
                    }
                    console.log(response);
                },
                error: error => {
                    console.error(error);
                }
            });
    }

}
