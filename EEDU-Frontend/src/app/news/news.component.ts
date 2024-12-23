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
import {UserService} from "../user/user.service";
import {MatPaginator, PageEvent} from "@angular/material/paginator";

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
        NgIf,
        MatPaginator
    ],
  templateUrl: './news.component.html',
  styleUrl: './news.component.scss'
})
@Injectable({
    providedIn: 'root', // Default dependency injection for the whole app
})
export class NewsComponent implements OnInit {
    pageIndex: number = 0;

    constructor(public http: HttpClient,
                public newsService: NewsService,
                public userService: UserService,
                public route: ActivatedRoute,
                public router: Router,
                public dialog: Dialog) {
    }

    articleList: PostModel[] = [];
    articleCount!: bigint;

    @ViewChild('container', {static: false}) container: ElementRef | undefined;

    ngOnInit(): void
    {
        this.route.params.subscribe((params) => {
            const index = params['pageIndex'] ? +params['pageIndex'] : 0;
            this.pageIndex = index;
            this.loadCount();
            this.loadArticles(index);
        });
    }

    public loadArticles(pageIndex: number)
    {
        this.newsService.getPosts(pageIndex).subscribe(() => {
            this.articleList = this.newsService.articleList;
            setTimeout(() => this.calculateColumns());
            this.route.params.subscribe((params) => {
                if(params['id']) {
                    this.openArticle(BigInt(+params['id']));
                }
            });
        });
    }

    public loadCount() {
        this.newsService.getCount().subscribe((articleCount) => {
            this.articleCount = articleCount;
        })
    }

    public nextPage(pageEvent: PageEvent): void
    {
        const newPageIndex = pageEvent.pageIndex;
        this.router.navigate([`/news/page/${newPageIndex}`]);
    }

    public getTimeString(timestamp: number): string
    {
        return new Date(Number(timestamp)).toDateString();
    }

    public hasPermission(): boolean {
        return this.userService.getUserData.hasPrivilege("ADMIN");
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

    protected calculateColumns(): number
    {
        // Source: https://stackblitz.com/edit/angular-dynamic-grid-list-cols-alprdz?file=src%2Fapp%2Fapp.component.html
        if (this.container) {
            const containerWidth = this.container.nativeElement.clientWidth;
            let screenDivision = Math.floor(containerWidth / 350);
            return (screenDivision > 0 ? screenDivision : 1);
        } else {
            return 1;
        }
    }

    protected readonly BigInt = BigInt;
}
