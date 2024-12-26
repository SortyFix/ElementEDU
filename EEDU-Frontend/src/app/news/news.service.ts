import {Injectable, model, OnInit} from '@angular/core';
import {PostModel} from "./post-model";
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {Observable, tap, timestamp} from "rxjs";
import {UserService} from "../user/user.service";

@Injectable({
  providedIn: 'root'
})
export class NewsService implements OnInit {
    constructor(public router: Router, public http: HttpClient, public userService: UserService) {
        console.log("Getting posts...")
        this.getPosts();
    }

    articleList: PostModel[] = [];

    public getTimeString(timestamp: number): string
    {
        return new Date(Number(timestamp)).toDateString();
    }

    public getPosts(pageIndex?: number): Observable<PostModel[]> {
        if(pageIndex == null)
        {
            pageIndex = 0;
        }

        return this.http.get<PostModel[]>(`http://localhost:8080/api/v1/blog/get/list?pageNumber=${pageIndex}`, {
            withCredentials: true
        }).pipe(
            tap((list) => {
                this.articleList = list;
                this.stringsToImages();
            })
        );
    }

    public getCount(): Observable<bigint>
    {
        return this.http.get<bigint>('http://localhost:8080/api/v1/blog/get/length', {
            withCredentials: true
        });
    }

    public getArticle(id: number): Observable<PostModel>
    {
        return this.http.get<PostModel>(`http://localhost:8080/api/v1/blog/get/${id}`, {
            withCredentials: true
        });
    }

    public stringsToImages() {
        this.articleList.forEach((postModel) => {
            let base64: string = postModel.thumbnailBlob;
            postModel.thumbnailBlob = `data:${this.getMimeType(base64)};base64,${base64}`;
        });
    }

    public getMimeType(base64: string): string | null {
        let indicator: string = base64.charAt(0);
        switch (indicator) {
            case '/':
                return 'image/jpg'
            case 'i':
                return 'image/png'
            case 'R':
                return 'image/gif'
            case 'U':
                return 'image/webp'
            default:
                return null
        }
    }

    ngOnInit(): void {
        this.getPosts();
    }
}
