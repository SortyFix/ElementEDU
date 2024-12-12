export class FileModel {
    constructor(
        public readonly id: bigint,
        public readonly dataDirectory: string,
        public readonly privileges: string[],
        public readonly tags: string[],
        public fileName: string
    ) { }
}
