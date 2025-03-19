import {AssessmentModel, GenericAssessment} from "./assessment/assessment-model";
import {GenericReducedUserModel, ReducedUserModel} from "../../../../reduced-user-model";

export interface GenericAssignmentInsightModel {
    user: GenericReducedUserModel,
    submitted: boolean,
    files: readonly string[],
    assessment?: GenericAssessment
}

export class AssignmentInsightModel {

    public constructor(
        private readonly _user: ReducedUserModel,
        private readonly _submitted: boolean,
        private readonly _files: readonly string[],
        private readonly _assessment: AssessmentModel | null
    ) {}

    public static fromObject(obj: GenericAssignmentInsightModel): AssignmentInsightModel {
        return new AssignmentInsightModel(
            ReducedUserModel.fromObject(obj.user),
            obj.submitted,
            obj.files,
            obj.assessment ? AssessmentModel.fromObject(obj.assessment) : null
        );
    }

    public static pushAssessment(assignment: AssignmentInsightModel, assessment: AssessmentModel): AssignmentInsightModel
    {
        return new AssignmentInsightModel(assignment.user, assignment.submitted, assignment.files, assessment);
    }

    public get user(): ReducedUserModel {
        return this._user;
    }

    public get submitted(): boolean {
        return this._submitted;
    }

    public get files(): readonly string[] {
        return this._files;
    }

    public get assessment(): AssessmentModel | null {
        return this._assessment;
    }
}
