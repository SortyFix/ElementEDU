export class LoginRequest {
    loginName: string;
    password: string;
    keepLoggedIn: boolean = false;

    constructor(loginName: string, password: string, keepLoggedIn: boolean) {
        this.loginName = loginName;
        this.password = password
        this.keepLoggedIn = keepLoggedIn;
    }
}
