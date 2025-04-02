export class SimpleThemeEntity {
    public readonly id: bigint;
    public readonly name: string;

    constructor(id: bigint, name: string) {
        this.id = id;
        this.name = name;
    }
}
