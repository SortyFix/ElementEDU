export class ThemeEntity {
    public readonly id: bigint;
    public readonly name: string;

    public readonly backgroundColor_r: number;
    public readonly backgroundColor_g: number;
    public readonly backgroundColor_b: number;

    public readonly widgetColor_r: number;
    public readonly widgetColor_g: number;
    public readonly widgetColor_b: number;

    constructor(id: bigint, name: string, backgroundColor: number, widgetColor: number, textColor: number)
    {
        this.id = id;
        this.name = name;

        this.backgroundColor_r = backgroundColor;
        this.backgroundColor_g = backgroundColor;
        this.backgroundColor_b = backgroundColor;

        this.widgetColor_r = widgetColor;
        this.widgetColor_g = widgetColor;
        this.widgetColor_b = widgetColor;
    }
}
