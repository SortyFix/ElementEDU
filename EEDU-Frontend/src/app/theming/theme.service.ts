import {Injectable} from '@angular/core';
import {UserService} from "../user/user.service";
import {ThemeEntity} from "./theme-entity";

@Injectable({
    providedIn: 'root'
})
/**
 * This ThemeService provides methods to retrieve theme-related information
 * for UI elements. Apart from getter methods, this service also includes text color
 * logic based on the luminance of the given background for better readability.
 */
export class ThemeService {
    constructor(public userService: UserService) { }

    public loadTheme(): ThemeEntity {
        const theme: ThemeEntity = this.userService.getUserData.theme;
        theme.updateDeepAngularStyles();
        console.log(theme);
        return theme;
    }
}
