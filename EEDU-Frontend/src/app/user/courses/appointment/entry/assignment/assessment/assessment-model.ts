export interface GenericAssessment
{
    id: bigint;
    grade: number;
    feedback?: string
}

export class AssessmentModel {

    private static readonly BIT_MASK: 0xFFFFFFFFn = 0xFFFFFFFFn;

    public constructor(
        private _id: bigint,
        private _grade: number,
        private _feedback: string | null
    ) {}

    public static fromObject(obj: GenericAssessment): AssessmentModel
    {
        return new AssessmentModel(
            obj.id,
            obj.grade,
            obj.feedback || null,
        )
    }

    public get id(): bigint {
        return this._id;
    }

    public get appointment(): bigint
    {
        return (this.id >> 32n) & AssessmentModel.BIT_MASK;
    }

    public get user(): bigint
    {
        return this.id & AssessmentModel.BIT_MASK;
    }

    public get grade(): number {
        return this._grade;
    }

    public get feedback(): string | null {
        return this._feedback;
    }
}
