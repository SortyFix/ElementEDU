export class RoomModel {

    constructor(private _id: number, private _name: string) {}

    public static fromObject(obj: { id: number; name: string; }): RoomModel
    {
        return new RoomModel(obj.id, obj.name);
    }

    public get id(): number {
        return this._id;
    }

    public set id(value: number) {
        this._id = value;
    }

    public get name(): string {
        return this._name;
    }

    public set name(value: string) {
        this._name = value;
    }
}
