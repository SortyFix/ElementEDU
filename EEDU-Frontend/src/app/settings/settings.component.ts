import { Component } from '@angular/core';
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {HttpClient} from "@angular/common/http";
import {SimpleThemeEntity} from "../theming/simple-theme-entity";
import {map, Observable} from "rxjs";
import {FormControl, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatOption} from "@angular/material/autocomplete";
import {MatSelect} from "@angular/material/select";
import {AsyncPipe, NgForOf} from "@angular/common";
import {ThemeEntity} from "../theming/theme-entity";
import {UserService} from "../user/user.service";
import {MatButton} from "@angular/material/button";
import {ThemeService} from "../theming/theme.service";

@Component({
  selector: 'app-settings',
  standalone: true,
    imports: [
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

export class SettingsComponent {
    constructor(public http: HttpClient, public userService: UserService, public themeService: ThemeService) {
    }

    public themes!: Observable<SimpleThemeEntity[]>;
    public selectedTheme!: bigint;
    themeForm = new FormControl<SimpleThemeEntity | null>(null, Validators.required);

    ngOnInit(): void {
        this.themes = this.fetchAllThemes();

        this.themeForm.valueChanges.subscribe((selectedTheme) => {
            if (selectedTheme) {
                this.selectedTheme = selectedTheme.id;
            }
        });
    }

    public saveSettings()
    {
        let userData = localStorage.getItem("userData");
        if(userData)
        {
            let parsedUserData = JSON.parse(userData);
            this.setTheme(this.selectedTheme).subscribe(themeEntity => {
                parsedUserData.theme = themeEntity;
                localStorage.setItem("userData", JSON.stringify(parsedUserData));
                location.reload();
            });
        }
    }

    public setTheme(themeId: bigint) {
        const url: string = `http://localhost:8080/user/me/theme/set`;
        return this.http.put<ThemeEntity>(url, themeId, {
            withCredentials: true
        });
    }

    public fetchAllThemes() : Observable<SimpleThemeEntity[]> {
        const url: string = "http://localhost:8080/user/theme/all";
        return this.http.get<SimpleThemeEntity[]>(url, {withCredentials: true});
    }

    public listThemes() : Observable<string[]> {
        return this.themes.pipe(
            map(themes => themes.map(theme => theme.name))
        );
    }
}
