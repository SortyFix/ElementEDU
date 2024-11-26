import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {finalize, map, Observable, of, tap} from "rxjs";
import {UserModel} from "./user-model";
import {environment} from "../../environment/environment";

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
            return this.fetchUserData.pipe(tap({
                next: (value: UserModel): void => this.storeUserData(JSON.stringify(value)),
                error: (error: any): void  => {
                    if(error && 'status' in error && typeof error.status === 'number' && error.status === 403)
                    {
                        // logout when token expired
                        this.logout().subscribe();
                        return;
                    }
                    // idk??
                }
            }), map((): void => {}));
        }

        return this.fetchUserData.pipe(tap<UserModel>({
            next: (value: UserModel) => this.storeUserData(JSON.stringify(value))
        }), finalize((): void => { this._loaded = true; }), map((): void => {}));
    }

    public get getUserData(): UserModel
    {
        const userData: string | null = localStorage.getItem('userData')
        if (!this.isLoggedIn || !userData)
        {
            throw new Error("User is not logged in, or user data is corrupt.");
        }
        const userModel = UserModel.fromObject(JSON.parse(userData));
        userModel.theme.updateDeepAngularStyles();
        return userModel;
    }

    public get hasLoaded(): boolean
    {
        return this._loaded;   }

    public logout(): Observable<any> // TODO maybe move to login service??
    {
        if (!this.isLoggedIn)
        {
            return of();
        }

        const url: string = `${this.BACKEND_URL}/user/logout`;
        return this.http.get<any>(url, {withCredentials: true}).pipe(tap<any>({
            next: () => {
                localStorage.removeItem("userData");
                this.resetDeepAngularStyles();
            }
        }));
    }

    public resetDeepAngularStyles(): void{
        document.documentElement.style.setProperty('--text-color', 'rgb(0, 0, 0)');
        document.documentElement.style.setProperty('--background-color', 'rgb(255, 255, 255)');
        document.documentElement.style.setProperty('--widget-color', 'rgb(230, 230, 230)');
    }

    public get isLoggedIn(): boolean
    {
        return !!localStorage.getItem("userData");
    }

    private get fetchUserData(): Observable<UserModel>
    {
        const url: string = `${this.BACKEND_URL}/user/get`;
        return this.http.get<UserModel>(url, {withCredentials: true});
    }

    public get fetchAll(): Observable<UserModel[]>
    {
        const url: string = `${this.BACKEND_URL}/user/all`;
        return this.http.get<any[]>(url, { withCredentials: true }).pipe(
            map((response: any[]): UserModel[] =>
                response.map((element: any): UserModel => UserModel.fromObject(element))
            )
        );
    }

    private storeUserData(userData: string)
    {
        localStorage.setItem("userData", userData)
    }
}
