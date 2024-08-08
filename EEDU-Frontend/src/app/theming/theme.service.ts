import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {ThemeEntity} from "./theme-entity";
import {HttpClient} from "@angular/common/http";
import {log} from "@angular-devkit/build-angular/src/builders/ssr-dev-server";

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  constructor(private http: HttpClient)
  {
  }

  // Receives user theme via GET request with credentials (token).
  public fetchTheme() : Observable<ThemeEntity> {
      console.log("Fetching theme...");
      const url: string = "http://localhost:8080/user/me/theme/get";
      return this.http.get<ThemeEntity>(url, { withCredentials: true });
  }

    /**
     * Parses the current theme in storage to a JS object
     */
  public parseStorageTheme() {
      const theme = localStorage.getItem("userTheme");
      return theme ? JSON.parse(theme) : null;
  }

}

