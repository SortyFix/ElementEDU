import { Injectable } from '@angular/core';
import {PostCreateModel} from "./post-create-model";
import {PostModel} from "../post-model";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environment/environment";

@Injectable({
  providedIn: 'root'
})
export class ArticleCreationService {
    constructor(public http: HttpClient) { }

    files!: File[];

    createArticle(createModel: PostCreateModel)
    {
        let title: string = createModel.title;
        let author: string = createModel.author;
        let body: string = createModel.body;
        let multipartFile: File = this.files[0];
        let editPrivileges: string[] = createModel.editPrivileges;
        let tags: string[] = createModel.tags;

        const formData = new FormData();

        const postCreateModel: PostCreateModel = {
            title: title,
            author: author,
            body: body,
            thumbnailURL: null,
            editPrivileges: editPrivileges,
            tags: tags
        }

        console.log(postCreateModel);

        formData.append('createModel', new Blob([JSON.stringify(postCreateModel)], { type: 'application/json' }));

        if(multipartFile)
        {
            formData.append('multipartFile', multipartFile);
        }

        console.log(multipartFile);

        this.sendPostCreationRequest(formData);
    }

    sendPostCreationRequest(formData: FormData) {
        console.log(formData);
        this.http
            .post<PostModel>(`${environment.backendUrl}/blog/post`, formData, {
                withCredentials: true
            })
            .subscribe({
                next: (response) => {
                    console.log('Post created successfully:', response);
                    location.reload();
                },
                error: (error) => {
                    console.error('Error creating post:', error);
                }
            });
    }
}
