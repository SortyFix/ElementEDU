import {Injectable} from '@angular/core';
import { jwtDecode } from "jwt-decode";


@Injectable({
    providedIn: 'root'
})
export class AuthorizeServiceService {

    constructor(private jwtToken: string, private decodedToken: { [key: string]: string }) {
    }

    setJwtToken(token: string) {
        if (token) {
            this.jwtToken = token;
        }
    }

    decodeToken(token: string) {
        if(this.jwtToken)
        {
            this.decodedToken = jwtDecode(this.jwtToken);
        }
    }

    isTokenExpired()
    {
    }
}
