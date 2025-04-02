import {
    AssignmentCreateModel,
    AssignmentCreatePacket,
    GenericAssignmentCreateModel
} from "./assignment/assignment-create-model";

export interface GenericAppointmentUpdate {
    description: string;
    room?: { id: number };
    assignment?: GenericAssignmentCreateModel;
}

export interface AppointmentUpdatePacket {
    description?: string,
    room?: number,
    assignment?: AssignmentCreatePacket
}

export class AppointmentUpdateModel {

    public constructor(
        private readonly _description?: string,
        private readonly _room?: number,
        private readonly _assignment?: AssignmentCreateModel
    ) {}

    public get toPacket(): AppointmentUpdatePacket {
        return {
            description: this.description,
            room: this.room,
            assignment: this.assignment?.toPacket(),
        }
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

    public static fromObject(obj: GenericAppointmentUpdate): AppointmentUpdateModel {
        return new AppointmentUpdateModel(
            obj.description,
            obj.room?.id,
            obj.assignment ? AssignmentCreateModel.fromObject(obj.assignment) : undefined
        );
    }
}
