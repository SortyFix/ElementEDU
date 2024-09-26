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
}
