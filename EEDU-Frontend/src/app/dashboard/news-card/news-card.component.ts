import {Component, OnInit} from '@angular/core';
import {NewsService} from "../../news/news.service";
import {PostModel} from "../../news/post-model";
import {NgForOf, NgIf} from "@angular/common";

@Component({
    selector: 'app-news-card',
    standalone: true,
    imports: [
        NgForOf,
        NgIf
    ],
    templateUrl: './news-card.component.html',
    styleUrl: './news-card.component.scss'
})
export class NewsCardComponent implements OnInit {

    postList: PostModel[] = [];

    constructor(public newsService: NewsService) {
    }

    ngOnInit(): void {
        this.newsService.getPosts().subscribe(posts => {
            this.postList = posts.splice(0, 4);
        });
    }
}
