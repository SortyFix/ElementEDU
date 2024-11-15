import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {finalize, map, Observable, of, tap} from "rxjs";
import {UserModel} from "./user-model";
import {ThemeEntity} from "../theming/theme-entity";
import {environment} from "../../environments/environment";
import {GroupModel} from "./GroupModel";
import {PrivilegeModel} from "./PrivilegeModel";

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
            next: (value: UserModel): void => this.storeUserData(JSON.stringify(value))
        }), finalize((): void => { this._loaded = true; }), map((): void => {}));
    }

    public get getUserData(): UserModel
    {
        const userData: string | null = localStorage.getItem('userData')
        if (!this.isLoggedIn || !userData)
        {
            throw new Error("User is not logged in, or user data is corrupt.");
        }
        return this.toUser(userData);
    }

    public get hasLoaded(): boolean
    {
        return this._loaded;
    }

    public logout(): Observable<any> // TODO maybe move to login service??
    {
        if (!this.isLoggedIn)
        {
            console.log("wlll")
            return of();
        }

        const url = `${this.BACKEND_URL}/user/logout`;
        return this.http.get<any>(url, {withCredentials: true}).pipe(tap<any>({
            next: (): void => localStorage.removeItem("userData")
        }));
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
        return this.http.get<UserModel[]>(url, {withCredentials: true});
    }

    private storeUserData(userData: string)
    {
        localStorage.setItem("userData", userData)
    }

    private toUser(json: string): UserModel
    {
        const parsedJson: any = JSON.parse(json);
        const theme: any = parsedJson.theme;

        const themeEntity: ThemeEntity = new ThemeEntity(
            theme.id,
            theme.name,
            theme.backgroundColor_r,
            theme.backgroundColor_g,
            theme.backgroundColor_b,
            theme.widgetColor_r,
            theme.widgetColor_g,
            theme.widgetColor_b
        );
        themeEntity.updateDeepAngularStyles();

        const groups: GroupModel[] = parsedJson.groups.map((group: any): GroupModel => {
            const privileges: any = group.privileges.map((privilege: any): PrivilegeModel => new PrivilegeModel(privilege.name, privilege.level));

            return new GroupModel(BigInt(group.id), group.name, privileges);
        })

        return new UserModel(BigInt(parsedJson.id), parsedJson.firstName, parsedJson.lastName, parsedJson.loginName, parsedJson.userStatus, groups, themeEntity);
    }
}
