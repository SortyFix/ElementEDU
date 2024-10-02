import { Injectable } from '@angular/core';
import {UserService} from "../user/user.service";

@Injectable({
    providedIn: 'root'
})
export class ThemeService {
    constructor(public userService: UserService) { }

    public get getBackgroundColor(): string
    {
        const theme: any = this.userService.getUserData.theme;
        return `rgb(${theme.backgroundColor_r}, ${theme.backgroundColor_g}, ${theme.backgroundColor_b})`
    }

    public get getWidgetColor(): string
    {
        const theme: any = this.userService.getUserData.theme;
        return `rgb(${theme.widgetColor_r}, ${theme.widgetColor_g}, ${theme.widgetColor_b})`
    }

    public getTextColorByLuminance(r: number, g: number, b: number, title: boolean)
    {
        // Formula for relative luminance
        // See https://www.itu.int/dms_pubrec/itu-r/rec/bt/R-REC-BT.709-6-201506-I!!PDF-E.pdf, section 3.2
        const luminance: number = ((0.2126 * r) + (0.7152 * g) + (0.0722 * b));
        if(title)
        {
            return luminance > 220 ? 'darkblue' : 'rgb(220,220,220)';
        }
        return luminance > 220 ? 'rgb(0,0,0)' : 'rgb(220,220,220)';
    }

    public getTextColor(type: 'background' | 'widget', isTitle: boolean): string
    {
        const theme: any = this.userService.getUserData.theme;
        let r, g, b: number;
        switch (type) {
            case "background":
                r = theme.backgroundColor_r;
                g = theme.backgroundColor_g;
                b = theme.backgroundColor_b;
                break;
            case "widget":
                r = theme.widgetColor_r;
                g = theme.widgetColor_g;
                b = theme.widgetColor_b;
                break;
        }
        return this.getTextColorByLuminance(r, g, b, isTitle);
    }
}
