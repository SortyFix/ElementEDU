import {Component, HostListener, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {UserService} from "../user/user.service";
import {ThemeService} from "../theming/theme.service";

@Component({
  selector: 'app-sidebar',
  templateUrl: './abstract.component.html',
  styleUrls: ['./abstract.component.scss']
})

export class AbstractComponent implements OnInit {
    private _mobile: boolean = false;

    constructor(public router: Router, public userService: UserService, public themeService: ThemeService) { }

    sidebar_buttons = [
        {title:'Dashboard', icon_name: 'dashboard', route:'dashboard'},
        {title:'Courses', icon_name:'book_5', route: 'courses'},
        {title:'Timetable', icon_name:'calendar_view_week', route: 'timetable'},
        {title:'News', icon_name: 'newspaper', route: 'news'},
        {title:'Chat', icon_name: 'forum', route: 'chat'},
        {title:'Settings', icon_name: 'settings', route: 'settings'}
    ]

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
        this.isMobile()
    }

    ngOnInit(): void {
        this.isMobile();
    }
}
