export class PostModel {
    constructor(
        public readonly id: bigint,
        public readonly author: string,
        public readonly title: string,
        public readonly thumbnailBlob: Blob,
        public readonly body: string,
        public readonly timeOfCreation: number,
        public readonly readPrivileges: string[],
        public readonly editPrivileges: string[],
        public readonly tags: string[]){ }
}
