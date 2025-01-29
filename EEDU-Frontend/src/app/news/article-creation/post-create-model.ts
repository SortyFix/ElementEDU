export interface PostCreateModel {
    author: string;
    title: string;
    thumbnailURL: string | null;
    body: string;
    editPrivileges: string[];
    tags: string[];
}
