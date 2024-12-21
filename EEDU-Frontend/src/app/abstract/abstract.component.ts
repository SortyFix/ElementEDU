import {Component, HostListener, OnInit} from '@angular/core';
import {Router, RouterLink, RouterOutlet} from "@angular/router";
import {UserService} from "../user/user.service";
import {MatIcon} from "@angular/material/icon";
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from "@angular/material/sidenav";
import {MatButton, MatIconButton} from "@angular/material/button";
import {NgForOf, NgIf, NgStyle} from "@angular/common";

@Component({
    selector: 'app-abstract',
    templateUrl: './abstract.component.html',
    imports: [
        MatIcon,
        MatDrawer,
        MatDrawerContainer,
        MatButton,
        MatIconButton,
        NgForOf,
        RouterOutlet,
        NgStyle,
        RouterLink,
        NgIf,
        MatDrawerContent
    ],
    styleUrls: ['./abstract.component.scss']
})

export class AbstractComponent implements OnInit {
    private _mobile: boolean = false;
    private _portrait: boolean = false;

    constructor(public router: Router, public userService: UserService) { }

    sidebar_buttons = [
        {title:'Dashboard', icon_name: 'dashboard', route:'dashboard'},
        {title:'Courses', icon_name:'book_5', route: 'courses'},
        {title:'Timetable', icon_name:'calendar_view_week', route: 'timetable'},
        {title:'News', icon_name: 'newspaper', route: 'news'},
        {title:'Chat', icon_name: 'forum', route: 'chat'},
        {title:'Settings', icon_name: 'settings', route: 'settings'}
    ]

    responsiveContent() {
        return {

        };
    }

    public get user()
    {
        return this.userService.getUserData;
    }

    public logout() {
        this.userService.logout().subscribe(value => console.log(value));
    }

    private isMobile()
    {
        this._mobile = window.innerWidth <= 600;
    }

    private isPortrait()
    {
        this._portrait = window.innerHeight > window.innerWidth;
    }

    get mobile(): boolean {
        return this._mobile;
    }

    get portrait(): boolean {
        return this._portrait;
    }

    @HostListener("window:resize") public onResize()
    {
        this.isMobile();
        this.isPortrait();
    }

    ngOnInit(): void {
        this.isMobile();
        this.isPortrait();
    }
}
