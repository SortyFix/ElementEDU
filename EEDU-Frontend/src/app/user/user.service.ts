import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {finalize, map, Observable, of, tap} from "rxjs";
import {UserEntity} from "./user-entity";
import {LoginData} from "./login/authentication/login-data/login-data";
import {LoginRequest} from "./login/authentication/login-data/login-request";
import {CredentialMethod} from "./login/authentication/login-data/credential-method";

@Injectable({
    providedIn: 'root'
})
export class UserService
{
    private _loaded: boolean = false;

    constructor(private http: HttpClient)
    {
    }

    public loadData(): Observable<void>
    {
        this._loaded = this.isLoggedIn;
        if (this._loaded)
        {
            return of();
        }
        return this.fetchUserData.pipe(tap<UserEntity>({
            next: (value: UserEntity) => this.storeUserData(JSON.stringify(value))
        }), finalize((): void => { this._loaded = true; }), map((): void => {}));
    }

    public get getUserData(): UserEntity
    {
        const userData: string | null = localStorage.getItem('userData')
        if (!this.isLoggedIn || !userData)
        {
            throw new Error("User is not logged in, or user data is corrupt.");
        }
        return JSON.parse(userData);
    }

    public get hasLoaded(): boolean
    {
        return this._loaded;
    }

    public logout(): Observable<any>
    {
        if (!this.isLoggedIn)
        {
            return of();
        }

        const url = "http://localhost:8080/user/logout";
        return this.http.get<any>(url, {withCredentials: true}).pipe(tap<any>({
            next: () => localStorage.removeItem("userData")
        }));
    }

    public get isLoggedIn(): boolean
    {
        return !!localStorage.getItem("userData");
    }

    public request(data: LoginRequest): Observable<LoginData>
    {
        const url = "http://localhost:8080/user/login";
        return this.http.post<string>(url, data,
            {responseType: "text" as "json"}
        ).pipe(tap<string>({}), map((token) => new LoginData(data.loginName, token)));
    }

    public selectCredential(credential: CredentialMethod, data: LoginData): Observable<void>
    {
        const url: string = "http://localhost:8080/user/login/credentials/select/" + credential;
        return this.http.get<string>(url, {
            responseType: "text" as "json",
            headers: {"Authorization": "Bearer " + data.token},
            withCredentials: true
        }).pipe(tap(value => data.token = value), map((): void => {}));
    }

    public verifyPassword(password: string, loginData: LoginData): Observable<string>
    {
        const url = "http://localhost:8080/user/login/credentials/verify";
        return this.http.post<string>(url, password, {
            responseType: "text" as "json",
            headers: {"Authorization": "Bearer " + loginData.token},
            withCredentials: true
        }).pipe(tap<string>({next: () => this.loadData().subscribe()}));
    }

    private get fetchUserData(): Observable<UserEntity>
    {
        const url: string = "http://localhost:8080/user/get";
        return this.http.get<UserEntity>(url, {withCredentials: true});
    }

    private storeUserData(userData: string)
    {
        localStorage.setItem("userData", userData)
    }
}
