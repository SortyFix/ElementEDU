export class ThemeEntity {
    public readonly id: bigint;
    public readonly name: string;
    public readonly backgroundColor: number;
    public readonly widgetColor: number;
    public readonly textColor: number;

    constructor(id: bigint, name: string, backgroundColor: number, widgetColor: number, textColor: number)
    {
        this.id = id;
        this.name = name;
        this.backgroundColor = backgroundColor;
        this.widgetColor = widgetColor;
        this.textColor = textColor;
    }
}
