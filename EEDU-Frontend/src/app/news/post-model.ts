export interface PostModel {
    id: bigint,
    author: string,
    title: string,
    thumbnailBlob: Blob,
    body: string,
    timeOfCreation: bigint,
    readPrivileges: string[],
    editPrivileges: string[],
    tags: string[]
}
