import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UserService} from "../user.service";
import {LoginRequest} from "./login-data/login-request";
import {map, Observable, OperatorFunction, tap} from "rxjs";
import {LoginData} from "./login-data/login-data";
import {CredentialMethod} from "./login-data/credential-method";
import {jwtDecode, JwtPayload} from "jwt-decode";
import {environment} from "../../../environment/environment";

/**
 * Service responsible for handling user authentication and credential management.
 *
 * This service provides methods to set up, enable, select, and verify credentials
 * for a user login session. It also manages the login session and the user's login data.
 *
 * The service is injectable and provided as a singleton across the application,
 * ensuring that only one instance exists.
 *
 * @Injectable providedIn: "root" allows this service to be available globally in the application.
 */
@Injectable({
    providedIn: 'root'
})
export class AuthenticationService {

    private readonly BACKEND_URL: string = environment.backendUrl;

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
        const url = `${this.BACKEND_URL}/user/login` + (advanced ? "/advanced" : "");
        // send credential in case of advanced login
        return this.http.post<string>(url, data, {
            responseType: "text" as "json", withCredentials: true
        }).pipe(map((token: string): void => {this._loginData = new LoginData(data.loginName, token)}));
    }

    /**
     * Initiates the creation of a credential for login.
     *
     * @param credential    The method of credential to be set up (e.g., password, OTP).
     * @param additionalData Optional additional data required for credential setup (default is {@code undefined}).
     * @returns             An {@link Observable} that emits a string response containing the status or result of the request.
     * @throws              Will throw an error if login data is not present.
     */
    public setupCredential(credential: CredentialMethod, additionalData: any = undefined): Observable<string> {

        if (!this.loginData) {
            throw new Error();
        }

        const createModel: { method: CredentialMethod, temporary: boolean, data: string | undefined } = {
            method: credential, temporary: false, data: additionalData
        }

        const url: string = `${this.BACKEND_URL}/user/login/credentials/create`;
        return this.http.post<string>(url, createModel, {
            responseType: "text" as "json",
            withCredentials: true,
            headers: {"Authorization": "Bearer " + this.loginData.token}
        }).pipe(this.tokenValidator);
    }

    public enableCredential(secret: string): Observable<void> {

        if (!this.loginData) {
            throw new Error();
        }

        const url: string = `${this.BACKEND_URL}/user/login/credentials/enable/${this.loginData.credential?.toString()}`;
        return this.http.post<string>(url, secret, {
            responseType: "text" as "json",
            withCredentials: true,
            headers: {"Authorization": "Bearer " + this.loginData.token}
        }).pipe(this.tokenValidator, map((): void => {}));
    }

    /**
     * Selects a credential method for the current login session.
     *
     * @param credential The method of credential to be selected (e.g., password, OTP).
     * @returns          An {@link Observable} that emits a {@code void} response upon successful credential selection.
     *                   This updates the current token in the user's login data.
     * @throws           Will throw an error if login data is not present.
     */
    public selectCredential(credential: CredentialMethod): Observable<void> {

        if (!this.loginData) {
            throw new Error();
        }

        let url: string = `${this.BACKEND_URL}/user/login/credentials/select/${credential}`;
        if (this.loginData.credentialRequired) {
            url = `${this.BACKEND_URL}/user/login/credentials/create/select/${credential}`;
        }

        return this.http.get<string>(url, {
            responseType: "text" as "json",
            headers: {"Authorization": "Bearer " + this.loginData.token},
            withCredentials: true
        }).pipe(this.tokenValidator, map((): void => {}));
    }

    public verifyCredential(secret: string): Observable<string> {
        const url = `${this.BACKEND_URL}/user/login/credentials/verify`;
        return this.http.post<string>(url, secret, {
            responseType: "text" as "json",
            headers: {"Authorization": "Bearer " + this.loginData?.token},
            withCredentials: true
        }).pipe(this.tokenValidator);
    }


    /**
     * Completes the login process by loading the user's data and setting the login state.
     * <p>
     * This will only be executed when the user is not logged in yet,
     * otherwise this method will do nothing.
     *
     * This method is called internally upon successful verification or credential actions.
     */
    private finishLogin() {
        if (!this.userService.isLoggedIn) {
            this.userService.loadData().subscribe({next: (): void => this._loginData = undefined});
        }
    }

    /**
     * Retrieves the current login data.
     * This might be null. It must be populated by {@link requestAuthorization}.
     *
     * @returns The current {@link LoginData}, or {@code undefined} if no login data is present.
     */
    public get loginData(): LoginData | undefined {
        return this._loginData;
    }

    /**
     * Resets the current login data, clearing any authorization flow
     */
    public reset(): void {
        this._loginData = undefined;
    }

    private get tokenValidator(): OperatorFunction<any, any> {
        return tap({next: this.validateToken.bind(this)});
    }

    private validateToken(value: any): void {
        if (typeof value !== 'string' || value.length < 1 || !this.loginData) {
            return;
        }

        try {
            const decoded: JwtPayload = jwtDecode(value);
            this.loginData.token = value;
            if (decoded.sub === 'AUTHORIZED') {
                this.finishLogin();
            }
        } catch (ignored: any) {}
    }
}
