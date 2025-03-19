export interface GenericAssessmentCreateModel
{
    appointment: number;
    user: bigint;
    feedback?: string;
}

export interface AssessmentCreateModelPacket
{
    appointment: number;
    user: number;
    feedback: string | null;
}

export class AssessmentCreateModel
{
    public constructor(
        private readonly _appointment: number,
        private readonly _user: bigint,
        private readonly _feedback: string | null,
    ) {}

    public static fromObject(obj: GenericAssessmentCreateModel): AssessmentCreateModel {
        return new AssessmentCreateModel(
            obj.appointment,
            obj.user,
            obj.feedback || null,
        )
    }

    public get appointment(): number {
        return this._appointment;
    }

    public get user(): bigint {
        return this._user;
    }

    public get feedback(): string | null {
        return this._feedback;
    }

    public get toPacket(): AssessmentCreateModelPacket
    {
        return {
            user: Number(this.user),
            appointment: this.appointment,
            feedback: this.feedback
        }
    }
}
