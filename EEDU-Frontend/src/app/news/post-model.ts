export class PostModel {
    constructor(
        public readonly id: bigint,
        public readonly author: string,
        public readonly title: string,
        public thumbnailBlob: string,
        public readonly body: string,
        public readonly timeOfCreation: number,
        public readonly readPrivileges: string[],
        public readonly editPrivileges: string[],
        public readonly tags: string[]){ }
}
