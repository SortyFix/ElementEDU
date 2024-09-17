export class LoginRequest {

    public readonly loginName: string;
    public readonly keepLoggedIn: boolean = false;

    constructor(loginName: string, keepLoggedIn: boolean) {
        this.loginName = loginName;
        this.keepLoggedIn = keepLoggedIn;
    }
}
