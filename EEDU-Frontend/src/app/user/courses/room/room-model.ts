export interface GenericRoom {id: string;}

export class RoomModel {

    constructor(private readonly _id: string) {}

    public get id(): string { return this._id; }

    public static fromObject(obj: GenericRoom): RoomModel { return new RoomModel(obj.id); }
}
