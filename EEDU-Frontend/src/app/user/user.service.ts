import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {finalize, map, Observable, of, tap} from "rxjs";
import {UserModel} from "./user-model";
import {environment} from "../../environment/environment";
import {ReducedUserModel} from "./reduced-user-model";
import {MatSnackBar} from "@angular/material/snack-bar";

@Injectable({
    providedIn: 'root'
})
export class UserService
{
    private readonly BACKEND_URL: string = environment.backendUrl;
    private _loaded: boolean = false;

    public constructor(private _http: HttpClient, private _snackBar: MatSnackBar) {}

    private get http(): HttpClient {
        return this._http;
    }

    public loadData(): Observable<void>
    {
        this._loaded = this.isLoggedIn;
        if (this._loaded)
        {
            return this.fetchUserData.pipe(tap({
                next: (value: UserModel): void => this.storeUserData(JSON.stringify(value)),
                error: (error: any): void  => {
                    if(error && 'status' in error && typeof error.status === 'number')
                    {
                        // noinspection FallThroughInSwitchStatementJS
                        switch (error.status) { // fall through
                            // @ts-ignore
                            case 404:
                                this._snackBar.open("The account you are using seems not to exist.")
                            case 403:
                                this.logout().subscribe();
                                break;
                        }
                        // logout when token expired
                        return;
                    }
                }
            }), map((): void => {}));
        }

        return this.fetchUserData.pipe(tap<UserModel>({
            next: (value: UserModel): void => this.storeUserData(JSON.stringify(value)),
        }), finalize((): void => { this._loaded = true; }), map((): void => {}));
    }

    /**
     * Retrieves the user data from local storage.
     *
     * This getter fetches and parses the user data from local storage.
     *
     * @returns a {@link UserModel} instance containing the users data.
     * @throws {Error} if the user is not logged in or the stored data is corrupt.
     */
    public get getUserData(): UserModel
    {
        const userData: string | null = localStorage.getItem('userData')
        if (!this.isLoggedIn || !userData)
        {
            throw new Error("User is not logged in, or user data is corrupt.");
        }
        const userModel: UserModel = UserModel.fromObject(JSON.parse(userData));
        userModel.theme.updateDeepAngularStyles();
        return userModel;
    }

    /**
     * checks if the user data has been loaded.
     *
     * This getter returns the state of the _loaded attribute, showing whether
     * the user data has been successfully fetched and processed.
     *
     * @returns true if the user data has been loaded, false otherwise.
     */
    public get hasLoaded(): boolean
    {
        return this._loaded;
    }

    /**
     * Logs the user out and clears their data.
     *
     * This method sends a logout request to the backend.
     * If successful, it removes the users data from local storage.
     *
     * @returns an observable that completes when the logout operation is finished
     */
    public logout(): Observable<void>
    {
        if (!this.isLoggedIn)
        {
            return of();
        }

        const url: string = `${this.BACKEND_URL}/user/logout`;
        return this.http.get<void>(url, {withCredentials: true}).pipe(tap<any>({
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

    /**
     * Checks if the user is currently logged in.
     *
     * This getter determines the login status of the user by checking the presence of the
     * userData item in the local storage.
     *
     * @returns true if the user is logged in, false otherwise.
     */
    public get isLoggedIn(): boolean
    {
        return !!localStorage.getItem("userData");
    }

    /**
     * Fetches the full data of the currently authenticated user.
     *
     * This private method is used internally to retrieve the full details of the authenticated user from the backend.
     *
     * @returns aAn observable that holds a {@link UserModel} containing the full details of the authenticated user.
     */
    private get fetchUserData(): Observable<UserModel>
    {
        const url: string = `${this.BACKEND_URL}/user/get`;
        return this.http.get<UserModel>(url, {withCredentials: true});
    }

    /**
     * Fetches the full data of all available users.
     *
     * This method retrieves an array of {@link UserModel}, containing the full details of all users in the system.
     *
     * @returns An observable that holds an array of {@link UserModel}.
     */
    public get fetchAll(): Observable<UserModel[]>
    {
        const url: string = `${this.BACKEND_URL}/user/all`;
        return this.http.get<any[]>(url, { withCredentials: true }).pipe(
            map((response: any[]): UserModel[] =>
                response.map((element: any): UserModel => UserModel.fromObject(element))
            )
        );
    }

    /**
     * Fetches a reduced version of the {@link UserModel} for a specific user.
     *
     * This method retrieves a {@link ReducedUserModel}, which contains reduced user data.
     * It is primarily used when a user with restricted permissions accesses another user's information.
     *
     * @param userId the unique identifier of the user whose reduced information is requested.
     * @returns an observable that holds an array of {@link ReducedUserModel}.
     */
    public fetchReduced(userId: bigint)
    {
        const url: string = `${this.BACKEND_URL}/user/get/${userId}/reduced`;
        return this.http.get<any[]>(url, { withCredentials: true }).pipe(
            map((response: any[]): ReducedUserModel[] =>
                response.map((element: any): ReducedUserModel => ReducedUserModel.fromObject(element))
            )
        );
    }

    /**
     * Fetches reduced versions of all available users.
     *
     * This method retrieves an array of {@link ReducedUserModel}, containing reduced user data
     * for all users in the system. It is typically used for listing users with restricted details
     * in scenarios where full user data is not required or permitted.
     *
     * @returns an observable that holds an array of {@link ReducedUserModel}.
     */
    public get fetchAllReduced(): Observable<ReducedUserModel[]>
    {
        const url: string = `${this.BACKEND_URL}/user/all/reduced`;
        return this.http.get<any[]>(url, { withCredentials: true }).pipe(
            map((response: any[]): ReducedUserModel[] =>
                response.map((element: any): ReducedUserModel => ReducedUserModel.fromObject(element))
            )
        );
    }

    private storeUserData(userData: string)
    {
        localStorage.setItem("userData", userData)
    }
}
