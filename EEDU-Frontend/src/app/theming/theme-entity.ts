export class ThemeEntity {
    public readonly id: bigint;
    public readonly name: string;

    public readonly backgroundColor_r: number;
    public readonly backgroundColor_g: number;
    public readonly backgroundColor_b: number;

    public readonly widgetColor_r: number;
    public readonly widgetColor_g: number;
    public readonly widgetColor_b: number;

    constructor(id: bigint, name: string, backgroundColor_r: number, backgroundColor_g: number, backgroundColor_b: number, widgetColor_r: number, widgetColor_g: number, widgetColor_b: number)
    {
        this.id = id;
        this.name = name;

        this.backgroundColor_r = backgroundColor_r;
        this.backgroundColor_g = backgroundColor_g;
        this.backgroundColor_b = backgroundColor_b;

        this.widgetColor_r = widgetColor_r;
        this.widgetColor_g = widgetColor_g;
        this.widgetColor_b = widgetColor_b;
    }



    /**
     * Returns the background color as a string matching CSS syntax.
     * @returns {string} The widget color as a 'rgb(r, g, b)' CSS argument.
     */
    public get getBackgroundColor(): string
    {
        return `rgb(${this.backgroundColor_r}, ${this.backgroundColor_g}, ${this.backgroundColor_b})`
    }

    /**
     * Returns the widget color as a string matching CSS syntax.
     * @returns {string} The widget color as a 'rgb(r, g, b)' CSS argument.
     */
    public get getWidgetColor(): string
    {
        return `rgb(${this.widgetColor_r}, ${this.widgetColor_g}, ${this.widgetColor_b})`
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
                r = this.backgroundColor_r;
                g = this.backgroundColor_g;
                b = this.backgroundColor_b;
                break;
            case "widget":
                r = this.widgetColor_r;
                g = this.widgetColor_g;
                b = this.widgetColor_b;
                break;
        }
        return this.getTextColorByLuminance(r, g, b, isTitle);
    }

    /**
     * Updates the global CSS variables that control deep Angular styling configurations.
     *
     * These CSS variables are defined in the global styles (':root' in the styles.scss) and can be accessed
     * by CSS rules that use `var(--floating-label-color)` and `var(--background-color)`.
     *
     * @see getTextColor
     * @see getBackgroundColor
     */
    public updateDeepAngularStyles(): void
    {
        document.documentElement.style.setProperty('--floating-label-color', this.getTextColor("background", false));
        document.documentElement.style.setProperty('--background-color', this.getBackgroundColor);
    }
}
