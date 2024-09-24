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
export class AuthenticationService
{

    /**
     * Constructs a new instance of this class and injects the required services.
     *
     * @param http         The {@link HttpClient} service used to send HTTP requests to the backend API.
     * @param userService  The {@link UserService} used to manage and load user-specific data.
     */
    constructor(private http: HttpClient, private userService: UserService)
    { }

    /**
     * Sends a login request to the server, optionally performing an advanced login.
     *
     * @param data      The login request data containing the user's credentials.
     * @param advanced  Boolean flag to determine if an advanced login should be performed.
     *                  Defaults to {@label false}.
     * @return          An {@link Observable} which emits the login data containing
     *                  the login name and authentication token upon successful login.
     */
    public request(data: LoginRequest, advanced: boolean = false): Observable<LoginData>
    {
        const url = "http://localhost:8080/user/login" + (advanced ? "/advanced" : "");
        // send credential in case of advanced login
        return this.http.post<string>(url, data, {
            responseType: "text" as "json", withCredentials: true
        }).pipe(tap<string>({}), map((token) => new LoginData(data.loginName, token)));
    }

    public setupCredential(credential: CredentialMethod, loginData: LoginData,
                              additionalData: any = undefined): Observable<string>
    {
        const createModel: { method: CredentialMethod, data: string | undefined } = {
            method: credential, data: additionalData
        }
        const url: string = "http://localhost:8080/user/login/credentials/create";
        return this.http.post<string>(url, createModel, {
            responseType: "text" as "json",
            withCredentials: true,
            headers: {"Authorization": "Bearer " + loginData.token}
        });
    }

    public enableCredential(credential: CredentialMethod, loginData: LoginData,
                           secret: any = undefined): Observable<string>
    {
        const url: string = `http://localhost:8080/user/login/credentials/enable/${credential.toString()}`;
        return this.http.post<string>(url, secret, {
            responseType: "text" as "json",
            withCredentials: true,
            headers: {"Authorization": "Bearer " + loginData.token}
        });
    }

    /**
     * Sends a request to select a specific credential method after login.
     *
     * @param credential  The selected credential method (e.g., password, biometric, etc.).
     * @param data        The current login data containing the user's login name and token.
     * @return            An {@link Observable} which emits when the credential is successfully
     *                    selected and updates the token in the `LoginData`.
     */
    public selectCredential(credential: CredentialMethod, data: LoginData): Observable<void>
    {
        const url: string = "http://localhost:8080/user/login/credentials/select/" + credential;
        return this.http.get<string>(url, {
            responseType: "text" as "json", headers: {"Authorization": "Bearer " + data.token}, withCredentials: true
        }).pipe(tap(value => data.token = value), map((): void => {}));
    }

    /**
     * Verifies a given credential (e.g., OTP, secret key) for the current login session.
     *
     * @param secret      The credential or secret to verify (e.g., an OTP code or password).
     * @param loginData   The current login data containing the user's login name and token.
     * @return            An {@link Observable} that emits the verification response.
     *                    The user's data is reloaded upon successful verification.
     */
    public verifyCredential(secret: string, loginData: LoginData): Observable<string>
    {
        const url = "http://localhost:8080/user/login/credentials/verify";
        return this.http.post<string>(url, secret, {
            responseType: "text" as "json",
            headers: {"Authorization": "Bearer " + loginData.token},
            withCredentials: true
        }).pipe(tap<string>({next: () => this.userService.loadData().subscribe()}));
    }
}
