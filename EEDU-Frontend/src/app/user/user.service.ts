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

    public loadData(): Observable<void>
    {
        if(this.loggedIn())
        {
            return of();
        }

        return this.getUserdata().pipe(tap<string>({
            next: value => this.storeUserData(value),
        }), map(() => {}));
    }

    public loggedIn(): boolean
    {
        return !!localStorage.getItem("userData");
    }

    private storeUserData(userData: any)
    {
        localStorage.setItem("userData", userData)
    }

    private getUserdata(): Observable<any>
    {
        return this.http.get<any>("http://localhost:8080/user/get", {withCredentials: true});
    }
}
