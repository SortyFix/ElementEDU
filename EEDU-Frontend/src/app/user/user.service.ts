import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {finalize, map, Observable, of, tap} from "rxjs";
import {UserEntity} from "./user-entity";
import {ThemeEntity} from "../theming/theme-entity";
import {environment} from "../../environments/environment";

@Injectable({
    providedIn: 'root'
})
export class UserService
{
    private readonly BACKEND_URL: string = environment.backendUrl;
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
        const parsedJson: any = JSON.parse(userData);
        const theme: any = parsedJson.theme;
        const themeEntity: ThemeEntity = new ThemeEntity(
            theme.id,
            theme.name,
            theme.backgroundColor_r,
            theme.backgroundColor_g,
            theme.backgroundColor_b,
            theme.widgetColor_r,
            theme.widgetColor_g,
            theme.widgetColor_b);

        return new UserEntity(parsedJson.id, parsedJson.firstName, parsedJson.lastName, parsedJson.loginName, parsedJson.userStatus, themeEntity);
    }

    public get hasLoaded(): boolean
    {
        return this._loaded;
    }

    public logout(): Observable<any> // TODO maybe move to login service??
    {
        if (!this.isLoggedIn)
        {
            return of();
        }

        const url = `${this.BACKEND_URL}/user/logout`;
        return this.http.get<any>(url, {withCredentials: true}).pipe(tap<any>({
            next: () => localStorage.removeItem("userData")
        }));
    }

    public get isLoggedIn(): boolean
    {
        return !!localStorage.getItem("userData");
    }

    private get fetchUserData(): Observable<UserEntity>
    {
        const url: string = `${this.BACKEND_URL}/user/get`;
        return this.http.get<UserEntity>(url, {withCredentials: true});
    }

    private storeUserData(userData: string)
    {
        localStorage.setItem("userData", userData)
    }
}
