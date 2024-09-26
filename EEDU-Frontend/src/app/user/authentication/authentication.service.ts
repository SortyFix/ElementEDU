import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UserService} from "../user.service";
import {LoginRequest} from "./login-data/login-request";
import {map, Observable, tap} from "rxjs";
import {LoginData} from "./login-data/login-data";
import {CredentialMethod} from "./login-data/credential-method";

@Injectable({
    providedIn: 'root'
})
export class AuthenticationService {

    private _loginData: LoginData | undefined;

    /**
     * Constructs a new instance of this class and injects the required services.
     *
     * @param http         The {@link HttpClient} service used to send HTTP requests to the backend API.
     * @param userService  The {@link UserService} used to manage and load user-specific data.
     */
    constructor(private http: HttpClient, private userService: UserService) {
    }

    /**
     * Sends a login request to the server, optionally performing an advanced login.
     *
     * @param data      The login request data containing the user's credentials.
     * @param advanced  Boolean flag to determine if an advanced login should be performed.
     *                  Defaults to {@label false}.
     * @return          An {@link Observable} which emits the login data containing
     *                  the login name and authentication token upon successful login.
     */
    public requestAuthorization(data: LoginRequest, advanced: boolean = false): Observable<void> {
        const url = "http://localhost:8080/user/login" + (advanced ? "/advanced" : "");
        // send credential in case of advanced login
        return this.http.post<string>(url, data, {
            responseType: "text" as "json", withCredentials: true
        }).pipe(map((token: string): void => {this._loginData = new LoginData(data.loginName, token)}));
    }

    public setupCredential(credential: CredentialMethod, additionalData: any = undefined): Observable<string> {

        if (!this.loginData) {
            throw new Error();
        }

        const createModel: { method: CredentialMethod, data: string | undefined } = {
            method: credential, data: additionalData
        }

        const url: string = "http://localhost:8080/user/login/credentials/create";
        return this.http.post<string>(url, createModel, {
            responseType: "text" as "json",
            withCredentials: true,
            headers: {"Authorization": "Bearer " + this.loginData.token}
        });
    }

    public enableCredential(secret: string, loginData: LoginData): Observable<void> {
        if (!this.loginData) {
            throw new Error();
        }

        const url: string = `http://localhost:8080/user/login/credentials/enable/${loginData.credential?.toString()}`;
        return this.http.post<string>(url, secret, {
            responseType: "text" as "json",
            withCredentials: true,
            headers: {"Authorization": "Bearer " + this.loginData.token}
        }).pipe(map((token: string): void => {
            this.loginData!.token = token
        }));
    }

    public selectCredential(credential: CredentialMethod): Observable<void> {

        if (!this.loginData) {
            throw new Error();
        }

        const url: string = "http://localhost:8080/user/login/credentials/select/" + credential;
        return this.http.get<string>(url, {
            responseType: "text" as "json",
            headers: {"Authorization": "Bearer " + this.loginData.token},
            withCredentials: true
        }).pipe(map((value: string): void => {
            this.loginData!.token = value
        }));
    }

    /**
     * Verifies a given credential (e.g., OTP, secret key) for the current login session.
     *
     * @param secret      The credential or secret to verify (e.g., an OTP code or password).
     * @param loginData   The current login data containing the user's login name and token.
     * @return            An {@link Observable} that emits the verification response.
     *                    The user's data is reloaded upon successful verification.
     */
    public verifyCredential(secret: string, loginData: LoginData): Observable<string> {
        const url = "http://localhost:8080/user/login/credentials/verify";
        return this.http.post<string>(url, secret, {
            responseType: "text" as "json",
            headers: {"Authorization": "Bearer " + loginData.token},
            withCredentials: true
        }).pipe(tap<string>({next: () => this.userService.loadData().subscribe()}));
    }

    public get loginData(): LoginData | undefined {
        return this._loginData;
    }

    public reset(): void {
        this._loginData = undefined;
    }
}
