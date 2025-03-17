import {Component, HostListener, OnInit} from '@angular/core';
import {Router, RouterLink, RouterOutlet} from "@angular/router";
import {UserService} from "../user/user.service";
import {WebsocketService} from "../chat/websocket.service";
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from "@angular/material/sidenav";
import {MatIcon} from "@angular/material/icon";
import {NgForOf, NgIf, NgStyle} from "@angular/common";
import {MatButton, MatIconButton} from "@angular/material/button";

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
    standalone: true,
    styleUrls: ['./abstract.component.scss']
})

export class AbstractComponent implements OnInit {
    private _mobile: boolean = false;

    constructor(public websocketService: WebsocketService, public router: Router, public userService: UserService) {
    }

    sidebar_buttons = [
        {title:'Dashboard', icon_name: 'dashboard', route:'dashboard'},
        {title:'Timetable', icon_name:'calendar_view_week', route: 'timetable'},
        {title:'News', icon_name: 'newspaper', route: 'news'},
        {title:'Chat', icon_name: 'forum', route: 'chat'},
        {title:'Issue sick note', icon_name: 'disabled_by_default', route: 'illness-notification'},
        // TODO: Make management exclusive
        {title:'Management', icon_name: 'design_services', route: 'management'},
        {title:'Settings', icon_name: 'settings', route: 'settings'}
    ]

    responsiveContent() {
        return {
            'padding-left': this.mobile ? '20px' : '20px',
            'width': this.mobile ? 'calc(100% - 50px)' : 'calc(100% - 100px)',
            'margin-left': this.mobile ? '' : '20px'
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

    get mobile(): boolean {
        return this._mobile;
    }

    @HostListener("window:resize") public onResize()
    {
        this.isMobile();
    }

    ngOnInit(): void {
        this.isMobile();
    }
}
