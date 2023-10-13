import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
    providedIn: 'root'
})
export class UserService {

    constructor(private http: HttpClient) { }

    // NOT SECURE; PROTOTYPE!
    requestLogin(loginName: string, password: string){
        const body: {loginName: string, password: string } = {loginName, password};

        this.http.post("http://localhost:8080/user/login", body)
            .subscribe({
                next: response => {
                    console.log(response);
                },
                error: error => {
                    console.error(error);
                }
            });
    }

}
