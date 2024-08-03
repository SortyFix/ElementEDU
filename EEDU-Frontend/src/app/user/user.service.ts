import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {finalize, map, Observable, of, tap} from "rxjs";
import {LoginRequest} from "./login/authentication/login-name-form/login-request";
import {UserEntity} from "./user-entity";

@Injectable({
    providedIn: 'root'
})
export class UserService
{
    private loaded: boolean = false;
    private token?: string;

    constructor(private http: HttpClient)
    {
    }

    public loadData(): Observable<void>
    {
        this.loaded = this.isLoggedIn();
        if (this.loaded)
        {
            return of();
        }
        return this.fetchUserData().pipe(tap<UserEntity>({
            next: (value: UserEntity) => this.storeUserData(JSON.stringify(value))
        }), finalize((): void => { this.loaded = true; }), map((): void => {}));
    }

    public get getUserData(): UserEntity
    {
        const userData: string | null = localStorage.getItem('userData')
        if(!this.isLoggedIn() || !userData)
        {
            throw new Error("User is not logged in, or user data is corrupt.");
        }
        return JSON.parse(userData);
    }

    public hasLoaded(): boolean
    {
        return this.loaded;
    }

    public logout(): Observable<any>
    {
        if (!this.isLoggedIn())
        {
            return of();
        }

        const url = "http://localhost:8080/user/logout";
        return this.http.get<any>(url, {withCredentials: true}).pipe(tap<any>({
            next: () => localStorage.removeItem("userData")
        }));
    }

    public isLoggedIn(): boolean
    {
        return !!localStorage.getItem("userData");
    }

    public request(data: LoginRequest): Observable<void>
    {
        const url = "http://localhost:8080/user/login";
        return this.http.post<string>(url, data, {responseType: "text" as "json"}).pipe(tap<string>({
            next: value => this.token = value
        }), map(() => {}));
    }

    public verifyPassword(password: string): Observable<string>
    {
        const url = "http://localhost:8080/user/login/credentials/verify";
        return this.http.post<string>(url, password, {
            responseType: "text" as "json", headers: {"Authorization": "Bearer " + this.token}, withCredentials: true
        }).pipe(tap<string>({
            next: () =>
            {
                this.token = undefined;
                this.loadData().subscribe();
            }
        }));
    }

    private fetchUserData(): Observable<UserEntity>
    {
        const url: string = "http://localhost:8080/user/get";
        return this.http.get<UserEntity>(url, {withCredentials: true});
    }

    private storeUserData(userData: string)
    {
        localStorage.setItem("userData", userData)
    }
}
