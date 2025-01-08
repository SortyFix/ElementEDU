import {Component, OnInit} from '@angular/core';
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {HttpClient} from "@angular/common/http";
import {SimpleThemeEntity} from "../theming/simple-theme-entity";
import {forkJoin, Observable} from "rxjs";
import {FormControl, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatOption} from "@angular/material/autocomplete";
import {MatSelect} from "@angular/material/select";
import {AsyncPipe, NgForOf} from "@angular/common";
import {ThemeModel} from "../theming/theme-model";
import {MatButton} from "@angular/material/button";
import {ThemeService} from "../theming/theme.service";
import {UserModel} from "../user/user-model";
import {FileService} from "../file/file.service";
import {UserService} from "../user/user.service";
import {UserListComponent} from "../user/user-list/user-list.component";

@Component({
  selector: 'app-settings',
  standalone: true,
    imports: [
        UserListComponent,
        MatFormField,
        ReactiveFormsModule,
        MatOption,
        MatSelect,
        MatLabel,
        AsyncPipe,
        NgForOf,
        MatButton
    ],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})

export class SettingsComponent implements OnInit {
    constructor(public userService: UserService, public http: HttpClient, public themeService: ThemeService, public fileService: FileService) {
    }

    public themes!: Observable<SimpleThemeEntity[]>;
    public selectedTheme!: bigint;
    public currentThemeName: string = this.getUserData().theme.name;
    themeForm = new FormControl<SimpleThemeEntity | null>(null, Validators.required);

    public THEME_URL: string = "api/v1/user";

    private _userList: UserModel[] = [];

    ngOnInit(): void {
        this.themes = this.fetchAllThemes();
        this.themeForm.valueChanges.subscribe((selectedTheme) => {
            if (selectedTheme) {
                this.selectedTheme = selectedTheme.id;
            }
        });

        this.userService.fetchAll.subscribe((users: UserModel[]): void => { this._userList = users });
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
    public processSettings(parsedUserData: any) {
        const theme$ = this.setTheme(this.selectedTheme);
        const observables = [theme$]; // add further observables along the way

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
    public setTheme(themeId: bigint) {
        const url: string = `http://localhost:8080/${this.THEME_URL}/me/theme/set`;
        return this.http.put<ThemeModel>(url, themeId, {
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
        const url: string = `http://localhost:8080/${this.THEME_URL}/theme/all`;
        return this.http.get<SimpleThemeEntity[]>(url, {withCredentials: true});
    }

    protected get userList(): UserModel[] {
        return this._userList;
    }
}
