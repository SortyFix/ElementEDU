import {Component, HostListener, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {UserService} from "../user/user.service";
import {WebsocketService} from "../chat/websocket.service";

@Component({
  selector: 'app-abstract',
  templateUrl: './abstract.component.html',
  styleUrls: ['./abstract.component.scss']
})

export class AbstractComponent implements OnInit {
    private _mobile: boolean = false;
    private _portrait: boolean = false;

    constructor(public websocketService: WebsocketService, public router: Router, public userService: UserService) {
    }

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
            'padding-left': this.mobile ? '20px' : '20px',
            'width': this.portrait ? '' : 'calc(100% - 100px)',
            'margin-left': this.portrait ? '' : '40px'
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
