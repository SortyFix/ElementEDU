import {Inject, Injectable} from '@angular/core';
import {UserService} from "../user/user.service";
import {DOCUMENT} from "@angular/common";

@Injectable({
    providedIn: 'root'
})
/**
 * This ThemeService provides methods to retrieve theme-related information
 * for UI elements. Apart from getter methods, this service also includes text color
 * logic based on the luminance of the given background for better readability.
 */
export class ThemeService {
    constructor(@Inject(DOCUMENT) private document: Document, public userService: UserService) { }

    public theme: any = this.userService.getUserData.theme;

    ngOnInit(): void {
        this.theme = this.userService.getUserData.theme;
    }

    /**
     * Returns the background color from local storage as a string matching CSS syntax.
     * @returns {string} The background color as a 'rgb(r, g, b)' CSS argument.
     */
    public get getBackgroundColor(): string
    {
        return `rgb(${this.theme.backgroundColor_r}, ${this.theme.backgroundColor_g}, ${this.theme.backgroundColor_b})`
    }

    /**
     * Returns the widget color from local storage as a string matching CSS syntax.
     * @returns {string} The widget color as a 'rgb(r, g, b)' CSS argument.
     */
    public get getWidgetColor(): string
    {
        return `rgb(${this.theme.widgetColor_r}, ${this.theme.widgetColor_g}, ${this.theme.widgetColor_b})`
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
            return luminance > 200 ? 'darkblue' : 'rgb(220,220,220)';
        }
        return luminance > 200 ? 'rgb(0,0,0)' : 'rgb(220,220,220)';
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
        let r, g, b: number;
        switch (type) {
            case "background":
                r = this.theme.backgroundColor_r;
                g = this.theme.backgroundColor_g;
                b = this.theme.backgroundColor_b;
                break;
            case "widget":
                r = this.theme.widgetColor_r;
                g = this.theme.widgetColor_g;
                b = this.theme.widgetColor_b;
                break;
        }
        return this.getTextColorByLuminance(r, g, b, isTitle);
    }

    public updateDeepAngularStyles(): void
    {
        this.document.documentElement.style.setProperty('--floating-label-color', this.getTextColor("background", false));
        this.document.documentElement.style.setProperty('--background-color', this.getBackgroundColor);
    }
}
