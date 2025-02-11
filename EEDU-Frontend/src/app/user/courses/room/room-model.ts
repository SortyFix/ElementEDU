
export interface GenericRoom {
    id: number;
    name: string;
}

export class RoomModel {

    constructor(
        private readonly _id: number,
        // this can be saved as number as it won't ever exceed 1.7976931348623157e+308, unless there have a massive
        // house, but in that case it most likely is not a school
        private readonly _name: string
    ) {}

    public static fromObject(obj: GenericRoom): RoomModel
    {
        return new RoomModel(obj.id, obj.name);
    }

    public get id(): number {
        return this._id;
    }

    public get name(): string {
        return this._name;
    }
}
