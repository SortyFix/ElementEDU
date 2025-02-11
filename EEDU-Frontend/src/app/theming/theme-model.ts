export class ThemeModel {
    constructor(public readonly id: bigint,
                public readonly name: string,
                public readonly backgroundColorR: number,
                public readonly backgroundColorG: number,
                public readonly backgroundColorB: number,
                public readonly widgetColorR: number,
                public readonly widgetColorG: number,
                public readonly widgetColorB: number)
    { }

    public static fromObject(obj: any): ThemeModel
    {
        return new ThemeModel(
            obj.id,
            obj.name,
            obj.backgroundColorR,
            obj.backgroundColorG,
            obj.backgroundColorB,
            obj.widgetColorR,
            obj.widgetColorG,
            obj.widgetColorB);
    }

    /**
     * Returns the background color as a string matching CSS syntax.
     * @returns {string} The widget color as a 'rgb(r, g, b)' CSS argument.
     */
    public get getBackgroundColor(): string
    {
        return `rgb(${this.backgroundColorR + 128}, ${this.backgroundColorG + 128}, ${this.backgroundColorB + 128})`
    }

    /**
     * Returns the widget color as a string matching CSS syntax.
     * @returns {string} The widget color as a 'rgb(r, g, b)' CSS argument.
     */
    public get getWidgetColor(): string
    {
        return `rgb(${this.widgetColorR + 128}, ${this.widgetColorG + 128}, ${this.widgetColorB + 128})`
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
     * Retrieves the appropriate theme information from local storage
     * and calculates the result via {@link getTextColorByLuminance()}.
     * @param type Either 'background' or 'widget', depending on the background of the text in question.
     * @param isTitle True if the text in question requires dark blue on a light background (See
     * @see getTextColorByLuminance
     */
    public getTextColor(type: 'background' | 'widget', isTitle: boolean): string
    {
        let r, g, b: number;
        switch (type) {
            case "background":
                r = this.backgroundColorR + 128;
                g = this.backgroundColorG + 128;
                b = this.backgroundColorB + 128;
                break;
            case "widget":
                r = this.widgetColorR + 128;
                g = this.widgetColorG + 128;
                b = this.widgetColorB + 128;
                break;
        }
        return this.getTextColorByLuminance(r, g, b, isTitle);
    }

    /**
     * Updates the global CSS variables that control deep Angular styling configurations.
     *
     * These CSS variables are defined in the global styles (':root' in the styles.scss) and can be accessed
     * by CSS rules that use `var(--text-color)`, `var(--widget-color)` and `var(--background-color)`.
     *
     * @see getTextColor
     * @see getBackgroundColor
     */
    public updateDeepAngularStyles(): void
    {
        document.documentElement.style.setProperty('--text-color', this.getTextColor("background", false));
        document.documentElement.style.setProperty('--background-color', this.getBackgroundColor);
        document.documentElement.style.setProperty('--widget-color', this.getWidgetColor);
    }
}
