export interface GenericAssignmentInsightModel {
    name: string,
    submitted: boolean,
    files: string[]
}

export class AssignmentInsightModel {

    public constructor(
        private readonly _name: string,
        private readonly _submitted: boolean,
        private readonly _files: readonly string[]
    ) {}

    public static fromObject(obj: GenericAssignmentInsightModel): AssignmentInsightModel {
        return new AssignmentInsightModel(obj.name, obj.submitted, obj.files);
    }

    public get name(): string {
        return this._name;
    }

    public get submitted(): boolean {
        return this._submitted;
    }

    public get files(): readonly string[] {
        return this._files;
    }
}
