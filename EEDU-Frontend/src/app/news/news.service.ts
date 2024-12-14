import {Injectable, OnInit} from '@angular/core';
import {PostModel} from "./post-model";
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {timestamp} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class NewsService implements OnInit {
    constructor(public router: Router, public http: HttpClient) {
        this.getAllPosts();
    }

    articleList!: PostModel[];

    public getTimeString(timestamp: number): string
    {
        return new Date(Number(timestamp)).toDateString();
    }

    public getAllPosts(): void {
        this.http.get<PostModel[]>("http://localhost:8080/api/v1/blog/get/list", {
            withCredentials: true
        }).subscribe((list) => {
            this.articleList = list;
        });
    }

    public getArticle(index: number): PostModel
    {
        const foundArticle: PostModel | undefined = this.articleList.find(article => Number(article.id) === index);

        if(!foundArticle) {
            throw new Error(`Article with id ${index} not found.`);
        }

        return foundArticle;
    }

    ngOnInit(): void {
        this.getAllPosts();
    }
}
