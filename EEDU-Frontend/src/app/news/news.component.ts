import {AfterViewInit, Component, ElementRef, Injectable, OnInit, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {PostModel} from "./post-model";
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {NgForOf, NgIf} from "@angular/common";
import {ActivatedRoute, Router} from "@angular/router";
import {NewsService} from "./news.service";
import {MatIcon} from "@angular/material/icon";
import {MatFabButton} from "@angular/material/button";
import {Dialog} from "@angular/cdk/dialog";
import {ArticleCreationComponent} from "./article-creation/article-creation.component";
import {FileUploadButtonComponent} from "../file/file-upload-button/file-upload-button.component";

@Component({
  selector: 'app-news',
  standalone: true,
    imports: [
        MatGridList,
        MatGridTile,
        NgForOf,
        MatIcon,
        MatFabButton,
        FileUploadButtonComponent,
        NgIf
    ],
  templateUrl: './news.component.html',
  styleUrl: './news.component.scss'
})
@Injectable({
    providedIn: 'root', // Default dependency injection for the whole app
})
export class NewsComponent implements AfterViewInit, OnInit {

    ngAfterViewInit(): void {
        this.calculateColumns();
    }

    constructor(public http: HttpClient, public newsService: NewsService, public route: ActivatedRoute, public router: Router, public dialog: Dialog) {
    }

    columns!: number;

    @ViewChild('container', {static: false}) container: ElementRef | undefined;

    ngOnInit(): void
    {
        this.newsService.getAllPosts();
        this.route.params.subscribe((params) => {
            if(params['id']) {
                this.openArticle(BigInt(+params['id']));
            }
        })
    }

    onBoxResize(): void {
        this.calculateColumns()
    }

    public getTimeString(timestamp: number): string
    {
        return new Date(Number(timestamp)).toDateString();
    }

    public openArticle(articleId: bigint)
    {
        this.router.navigate([`/news/`, articleId]);
    }

    public openArticleCreation()
    {
        let dialogRef = this.dialog.open(ArticleCreationComponent, {
            width: "80%"
        })
    }

    public thumbnailBlobExists(post: PostModel): boolean {
        return !!post.thumbnailBlob;
    }

    private calculateColumns(): void
    {
        // Source: https://stackblitz.com/edit/angular-dynamic-grid-list-cols-alprdz?file=src%2Fapp%2Fapp.component.html
        if (this.container) {
            const containerWidth = this.container.nativeElement.clientWidth;
            let screenDivision = Math.floor(containerWidth / 350);
            this.columns = (screenDivision > 0 ? screenDivision : 1);
        } else {
            this.columns = 1;
        }
    }
}
