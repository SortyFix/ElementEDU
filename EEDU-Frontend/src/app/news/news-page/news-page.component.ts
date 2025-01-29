import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {PostModel} from "../post-model";
import {NewsService} from "../news.service";
import {NgForOf, NgIf} from "@angular/common";
import {MarkdownComponent} from "ngx-markdown";
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import {MatChip, MatChipSet} from "@angular/material/chips";
import {FileUploadButtonComponent} from "../../file/file-upload-button/file-upload-button.component";

@Component({
  selector: 'app-news-page',
  standalone: true,
    imports: [
        NgIf,
        MarkdownComponent,
        MatChipSet,
        MatChip,
        NgForOf,
        FileUploadButtonComponent
    ],
  templateUrl: './news-page.component.html',
  styleUrl: './news-page.component.scss'
})
export class NewsPageComponent implements OnInit
{
    article!: PostModel;
    body!: any;

    constructor(public newsService: NewsService, public route: ActivatedRoute, public router: Router) {
    }

    ngOnInit(): void {
        marked.setOptions({
            gfm: true,
            breaks: true
        });

        const articleId = Number(this.route.snapshot.paramMap.get('id') ?? '0');
        console.log(articleId);
        this.newsService.getArticle(articleId).subscribe(model => {
            this.article = model;
            this.article.thumbnailBlob =
                `data:${this.newsService.getMimeType(this.article.thumbnailBlob)};base64,${this.article.thumbnailBlob}`;
            console.log(this.article);
            this.body = DOMPurify.sanitize(marked(this.article.body) as string);
        });
    }

}
