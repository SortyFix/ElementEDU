import {CredentialMethod} from "./credential-method";

export class LoginData {

    private readonly _loginName: string;
    private readonly _availableCredentials: CredentialMethod[];

    constructor(loginName: string, availableCredentials: CredentialMethod[])
    {
        this._loginName = loginName;
        this._availableCredentials = availableCredentials;
    }


    public get loginName(): string
    {
        return this._loginName;
    }

    public get availableCredentials(): CredentialMethod[]
    {
        return this._availableCredentials;
    }
}
