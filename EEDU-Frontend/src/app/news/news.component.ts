import {AfterViewInit, Component, ElementRef, Injectable, OnInit, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {PostModel} from "./post-model";
import {Observable, timestamp} from "rxjs";
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {NgForOf} from "@angular/common";
import {MatDialog} from "@angular/material/dialog";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-news',
  standalone: true,
    imports: [
        MatGridList,
        MatGridTile,
        NgForOf
    ],
  templateUrl: './news.component.html',
  styleUrl: './news.component.scss'
})
@Injectable({
    providedIn: 'root', // Default dependency injection for the whole app
})
export class NewsComponent implements AfterViewInit, OnInit {

    ngOnInit(): void
    {
        this.route.params.subscribe((params) => {
            if(params['id']) {
                this.openArticle(BigInt(+params['id']));
            }
        })
    }

    ngAfterViewInit(): void {
        this.calculateColumns();
    }

    constructor(public http: HttpClient, public dialog: MatDialog, public route: ActivatedRoute, public router: Router) {
        this.getAllPosts();
    }

    columns!: number;

    articleList!: PostModel[];

    @ViewChild('container', {static: false}) container: ElementRef | undefined;

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

    private calculateColumns(): void
    {
        // Source: https://stackblitz.com/edit/angular-dynamic-grid-list-cols-alprdz?file=src%2Fapp%2Fapp.component.html
        if (this.container) {
            const containerWidth = this.container.nativeElement.clientWidth;
            let screenDivision = Math.floor(containerWidth / 400);
            this.columns = (screenDivision > 0 ? screenDivision : 1);
        } else {
            this.columns = 1;
        }
    }

    public getAllPosts(): void {
        this.http.get<PostModel[]>("http://localhost:8080/api/v1/blog/get/list", {
            withCredentials: true
        }).subscribe((list) => {
            this.articleList = list;
        });
    }
}
