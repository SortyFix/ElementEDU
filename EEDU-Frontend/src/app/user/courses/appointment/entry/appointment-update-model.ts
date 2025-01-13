import {AssignmentCreateModel} from "./assignment-create-model";

export class AppointmentUpdateModel {

    public constructor(
        private readonly _description?: string,
        private readonly _assignment?: AssignmentCreateModel
    ) {}

    public static fromObject(obj: {
        description: string,
        assignment: AssignmentCreateModel
    }
    ): AppointmentUpdateModel {
        return new AppointmentUpdateModel(
            obj.description,
            obj.assignment
        );
    }

    public get description(): string | undefined {
        return this._description;
    }

    public get assignment(): AssignmentCreateModel | undefined {
        return this._assignment;
    }

    public get toPacket(): {
        description: string | undefined,
        assignment: AssignmentCreateModel | undefined,
    }
    {
        return {
            description: this.description,
            assignment: this.assignment,
        }
    }
}
