import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
    providedIn: 'root'
})
export class GroupService {

    private readonly BACKEND_URL: string = environment.backendUrl;


    constructor(private http: HttpClient) {}


}
