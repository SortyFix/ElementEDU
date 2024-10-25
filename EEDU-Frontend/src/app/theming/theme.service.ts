import { Injectable } from '@angular/core';
import {UserService} from "../user/user.service";

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

    /**
     * Returns the background color from local storage as a string matching CSS syntax.
     * @returns {string} The background color as a 'rgb(r, g, b)' CSS argument.
     */
    public get getBackgroundColor(): string
    {
        const theme: any = this.userService.getUserData.theme;
        return `rgb(${theme.backgroundColor_r}, ${theme.backgroundColor_g}, ${theme.backgroundColor_b})`
    }

    /**
     * Returns the widget color from local storage as a string matching CSS syntax.
     * @returns {string} The widget color as a 'rgb(r, g, b)' CSS argument.
     */
    public get getWidgetColor(): string
    {
        const theme: any = this.userService.getUserData.theme;
        return `rgb(${theme.widgetColor_r}, ${theme.widgetColor_g}, ${theme.widgetColor_b})`
    }

    /**
     * Uses the formula for relative luminance
     * (See https://www.itu.int/dms_pubrec/itu-r/rec/bt/R-REC-BT.709-6-201506-I!!PDF-E.pdf, section 3.2):
     *  <pre>
     *      luminance = (0.2126 * r) + (0.7152 * g) + (0.0722 * b)
     *  </pre>
     * to determine if the text gains best readability by being either white or black.
     * @param r Red
     * @param g Green
     * @param b Blue
     * @param title Should only be true if the desired text requires dark blue on a light background. Currently
     *              only used by the ElementEDU title, hence this parameter name.
     * @returns String consisting of the CSS argument required for intuitive use
     */
    public getTextColorByLuminance(r: number, g: number, b: number, title: boolean)
    {
        const luminance: number = ((0.2126 * r) + (0.7152 * g) + (0.0722 * b));
        if(title)
        {
            return luminance > 220 ? 'darkblue' : 'rgb(220,220,220)';
        }
        return luminance > 220 ? 'rgb(0,0,0)' : 'rgb(220,220,220)';
    }

    /**
     * Class to be called via HTML. Retrieves the appropriate theme information from local storage
     * and calculates the result via {@link getTextColorByLuminance()}.
     * @param type Either 'background' or 'widget', depending on the background of the text in question.
     * @param isTitle True if the text in question requires dark blue on a light background (See
     * {@link getTextColorByLuminance()}
     */
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
