import {Injectable} from '@angular/core';
import {LoginRequest} from "./authentication/login-data/login-request";
import {map, Observable, tap} from "rxjs";
import {LoginData} from "./authentication/login-data/login-data";
import {CredentialMethod} from "./authentication/login-data/credential-method";
import {HttpClient} from "@angular/common/http";
import {UserService} from "../user.service";

/**
 * Service responsible for handling user login, credential selection, and verification.
 * Provides methods to interact with the backend authentication API.
 *
 * This service is provided in the root of the application, meaning it will be
 * available as a singleton throughout the app.
 *
 * @Injectable ensures that Angular can inject the necessary dependencies into this service.
 */
@Injectable({
    providedIn: 'root'
})
export class LoginService
{
    /**
     * Constructs a new instance of this class and injects the required services.
     *
     * @param http         The {@link HttpClient} service used to send HTTP requests to the backend API.
     * @param userService  The {@link UserService} used to manage and load user-specific data.
     */
    constructor(private http: HttpClient, private userService: UserService) { }

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

    public setupCredential(credential: CredentialMethod, data: LoginData)
    {
        const url: string = "http://localhost:8080/user/login/credentials/select/" + credential;
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
