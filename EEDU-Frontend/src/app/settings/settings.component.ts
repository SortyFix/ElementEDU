import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {HttpClient} from "@angular/common/http";
import {SimpleThemeEntity} from "../theming/simple-theme-entity";
import {forkJoin, map, Observable} from "rxjs";
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatOption} from "@angular/material/autocomplete";
import {MatSelect} from "@angular/material/select";
import {AsyncPipe, NgForOf} from "@angular/common";
import {ThemeModel} from "../theming/theme-model";
import {MatButton} from "@angular/material/button";
import {UserModel} from "../user/user-model";
import {UserService} from "../user/user.service";
import {MatInputModule} from "@angular/material/input";
import {MatDivider} from "@angular/material/divider";
import {
    MatExpansionModule,
} from "@angular/material/expansion";
import {FullUserListComponent} from "../user/user-list/full-user-list/full-user-list.component";
import {environment} from "../../environment/environment";
import {CookieHelpers} from "../user/cookie/cookie-helpers";
import {CookieOptions} from "../user/cookie/cookie-options";

@Component({
    selector: 'app-settings',
    standalone: true,
    providers: [CookieHelpers],
    imports: [
        MatFormField,
        MatInputModule,
        ReactiveFormsModule,
        MatOption,
        MatSelect,
        MatLabel,
        AsyncPipe,
        NgForOf,
        MatButton,
        FormsModule,
        MatDivider,
        MatExpansionModule,
        FullUserListComponent
    ],
    templateUrl: './settings.component.html',
    styleUrl: './settings.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class SettingsComponent implements OnInit {
    constructor(public userService: UserService, public http: HttpClient, public cookieHelpers: CookieHelpers) {
    }

    public themes!: Observable<SimpleThemeEntity[]>;
    public selectedTheme!: bigint;
    public currentThemeName: string = this.getUserData().theme.name;
    themeForm = new FormControl<SimpleThemeEntity | null>(null, Validators.required);
    public feedbackText = "";

    ngOnInit(): void {
        this.themes = this.fetchAllThemes();
        this.themeForm.valueChanges.subscribe((selectedTheme) => {
            if (selectedTheme) {
                this.selectedTheme = selectedTheme.id;
            }
        });
    }

    /**
     * Parses user data JSON from local storage to a JS object,
     * providing a convenient way of accessing and modifying specific
     * certain attributes.
     *
     * @returns JavaScript object containing the stored JSON data
     */
    public getUserData() {
        let userData: string | null = localStorage.getItem("userData");
        if(userData)
        {
            return JSON.parse(userData);
        }
    }

    public get user(): UserModel {
        return this.userService.getUserData;
    }

    /**
     * Updates user data by overriding old with new data. This method
     * automatically stringifies the user data object passed as a parameter.
     *
     * @param newUserData JS object containing the updated user data.
     */
    public updateUserData(newUserData: any) {
        localStorage.setItem("userData", JSON.stringify(newUserData));
    }

    /**
     * Proxy method for setting changes. Injects necessary user data from
     * local storage into {@link processSettings()} as a parsed JSON.
     *
     * @example
     * <button mat-button (click)="saveSettings()">Save changes</button>
     */
    public saveSettings()
    {
        this.processSettings(this.getUserData());
    }

    /**
     * Processes user settings and updates the theme in local storage.
     * IT retrieves the newly selected changes and overrides the affected
     * data in local storage. If server-based settings are modified, an HTTP request
     * is initiated to update the backend with the new theme settings using JSON data.
     *
     * @param parsedUserData Parsed object representation of the userData JSON retrieved from local storage
     */
    public processSettings(parsedUserData: any): void {
        const theme$: Observable<ThemeModel> = this.setTheme(this.selectedTheme);
        console.log(theme$);
        const observables: Observable<ThemeModel>[] = [theme$]; // add further observables along the way

        forkJoin(observables).subscribe(([themeEntity]) => {
            parsedUserData.theme = themeEntity;
            this.updateUserData(parsedUserData);
            location.reload();
        });
    }

    /**
     * HTTP request carrying the theme ID as a request body for the backend to process.
     * Necessary for the theme storage process.
     *
     * @param themeId Theme ID to be set.
     * @returns Observable<ThemeEntity> carrying the full newly selected theme.
     */
    public setTheme(themeId: bigint): Observable<ThemeModel> {
        const url: string = `${environment.backendUrl}/user/me/theme/set`;
        return this.http.put<ThemeModel>(url, themeId, {
            withCredentials: true
        }).pipe(map(model => {
            return new ThemeModel(model.id, model.name,
                model.backgroundColorR + 128, model.backgroundColorG + 128, model.backgroundColorB + 128,
                model.widgetColorR + 128, model.widgetColorG + 128, model.widgetColorB + 128);
        }));
    }

    /**
     * Creates a cookie containing the received theme data.
     */
    public setThemeLocally(): void
    {
        this.getTheme(this.selectedTheme).subscribe((themeModel: ThemeModel): void => {
            let themeModelJson: string = JSON.stringify(themeModel);
            let options: CookieOptions = new CookieOptions();
            options.maxAge = 60 * 60 * 24 * 365;
            this.cookieHelpers.createCookie("theme", themeModelJson, options);
        });
    }

    public getTheme(themeId: bigint): Observable<ThemeModel> {
        const url: string = `${environment.backendUrl}/user/theme/get/${themeId}`;

        return this.http.get<ThemeModel>(url, {
            withCredentials: true
        });
    }

    /**
     * Fetches all themes as a SimpleThemeEntity array observable. Typically used for
     * a theme selection dropdown.
     *
     * @returns Observable<SimpleThemeEntity[]> containing all themes in a simplified
     *          id, name format.
     */
    public fetchAllThemes() : Observable<SimpleThemeEntity[]> {
        const url: string = `${environment.backendUrl}/user/theme/all`;
        return this.http.get<SimpleThemeEntity[]>(url, {withCredentials: true});
    }

    public openEmail(body: string) {
        const email = 'yonasnieder@gmail.com';
        const subject = 'Feedback';
        if(this.feedbackText != "")
        {
            window.location.href = `mailto:${email}?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;
        }
    }
}
