export class SubjectModel {
    constructor(
        public readonly id: bigint,
        public readonly name: string
    ) {}

    public static fromObject(object: any): SubjectModel {
        return new SubjectModel(BigInt(object.id), object.name);
    }
}
