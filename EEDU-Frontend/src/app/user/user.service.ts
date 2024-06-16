import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable, of, tap} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class UserService
{

    constructor(private http: HttpClient)
    {
    }


    public isLoggedIn(): Observable<void>
    {
        const userData = localStorage.getItem("userData");

        if(userData)
        {
            return of();
        }

        return this.getUserdata().pipe(tap<string>({
            next: value => localStorage.setItem("userData", value),
        }), map(() => {}));
    }

    private getUserdata(): Observable<any>
    {
        return this.http.get<any>("http://localhost:8080/user/get", {withCredentials: true});
    }
}
