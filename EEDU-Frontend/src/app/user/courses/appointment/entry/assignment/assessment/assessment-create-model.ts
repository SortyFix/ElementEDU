export interface GenericAssessmentCreateModel
{
    appointment: number;
    user: number;
    feedback?: string;
}

export class AssessmentCreateModel
{
    public constructor(
        private readonly _appointment: number,
        private readonly _user: number,
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

    public get user(): number {
        return this._user;
    }

    public get feedback(): string | null {
        return this._feedback;
    }
}
