export class FileModel {
    constructor(
        public readonly id: bigint,
        public readonly dataDirectory: string,
        public readonly privileges: string[],
        public readonly tags: string[],
        public fileName: string
    ) { }

    public static fromObject(obj: any): FileModel {
        return new FileModel(
            obj.id,
            obj.dataDirectory,
            obj.privileges,
            obj.tags,
            obj.fileName
        )
    }
}
