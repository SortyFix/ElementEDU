export class LoginRequest {
    loginName: string;
    keepLoggedIn: boolean = false;

    constructor(loginName: string, keepLoggedIn: boolean) {
        this.loginName = loginName;
        this.keepLoggedIn = keepLoggedIn;
    }
}
