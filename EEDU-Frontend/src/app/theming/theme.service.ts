import {Injectable} from '@angular/core';
import {UserService} from "../user/user.service";
import {ThemeModel} from "./theme-model";

@Injectable({
    providedIn: 'root'
})

export class ThemeService {
    constructor(public userService: UserService) { }

    public loadTheme(): ThemeModel {
        const theme: ThemeModel = this.userService.getUserData.theme;
        theme.updateDeepAngularStyles();
        console.log(theme);
        return theme;
    }
}
