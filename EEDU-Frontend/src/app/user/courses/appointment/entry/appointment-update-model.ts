import {AssignmentCreateModel} from "./assignment-create-model";

export class AppointmentUpdateModel {

    public constructor(
        private readonly _description?: string,
        private readonly _room?: number,
        private readonly _assignment?: AssignmentCreateModel
    ) {}

    public static fromObject(obj: {
        description: string,
        room?: number,
        assignment: AssignmentCreateModel
    }
    ): AppointmentUpdateModel {
        return new AppointmentUpdateModel(
            obj.description,
            obj.room,
            obj.assignment
        );
    }

    public get description(): string | undefined {
        return this._description;
    }

    public get assignment(): AssignmentCreateModel | undefined {
        return this._assignment;
    }

    public get room(): number | undefined {
        return this._room;
    }

    public get toPacket(): {
        description?: string,
        room?: number,
        assignment?: { description: string, submitUntil: number, publish: number },
    }
    {
        return {
            description: this.description,
            room: this.room,
            assignment: this.assignment?.toPacket(),
        }
    }
}
