import { Component } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {PostModel} from "./post-model";
import {Observable} from "rxjs";

@Component({
  selector: 'app-news',
  standalone: true,
  imports: [],
  templateUrl: './news.component.html',
  styleUrl: './news.component.scss'
})
export class NewsComponent {
    constructor(public http: HttpClient) {
        this.getAllPosts().subscribe((postModels: PostModel[]) => {
            console.log(postModels);
        });
    }

    public getAllPosts(): Observable<PostModel[]> {
        return this.http.get<PostModel[]>("http://localhost:8080/api/v1/blog/get/list", {
            withCredentials: true
        });
    }
}
