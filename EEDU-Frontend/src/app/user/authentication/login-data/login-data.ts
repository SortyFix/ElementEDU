import {CredentialMethod} from "./credential-method";
import {jwtDecode, JwtPayload} from "jwt-decode";

export class LoginData {

    private readonly _loginName: string;
    private readonly _availableCredentials: CredentialMethod[];
    private _token: string;
    private _decodedToken: JwtPayload & { available: CredentialMethod[] };

    constructor(loginName: string, token: string)
    {
        this._loginName = loginName;
        this._token = token;
        this._decodedToken = jwtDecode(token);
        this._availableCredentials = this._decodedToken.available;
    }

    public get loginName(): string
    {
        return this._loginName;
    }

    public get availableCredentials(): CredentialMethod[]
    {
        return this._availableCredentials;
    }

    public get token(): string
    {
        return this._token;
    }

    public set token(value: string)
    {
        this._token = value;
        this._decodedToken = jwtDecode(value);
    }

    private get decodedToken(): JwtPayload & { available: CredentialMethod[] }
    {
        return this._decodedToken;
    }

    public get credential(): CredentialMethod | undefined
    {
        if (this.decodedToken.sub !== 'CREDENTIAL_PENDING' && this.decodedToken.sub !== 'CREDENTIAL_CREATION_PENDING')
        {
            return undefined;
        }
        return this.decodedToken.available[0];
    }

    public get credentialRequired(): boolean
    {
        return this.decodedToken.sub == 'CREDENTIAL_REQUIRED' || this.decodedToken.sub == 'CREDENTIAL_CREATION_PENDING';
    }

    public get credentialTemporary(): boolean
    {
        return this.decodedToken && 'temporaryId' in this.decodedToken;
    }
}
