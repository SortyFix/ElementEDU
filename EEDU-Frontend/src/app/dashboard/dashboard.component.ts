import { Component } from '@angular/core';
import {ThemeService} from "../theming/theme.service";
import {UserService} from "../user/user.service";
import {CdkDragDrop} from "@angular/cdk/drag-drop";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
    constructor(public themeService: ThemeService, public userService: UserService) {
    }

    cards = [
        { title: 'Timetable', content: 'This is the content of card 1.' },
        { title: 'Homework', content: 'This is the content of card 2.' },
        { title: 'Latest news', content: 'This is the content of card 3.' },
        { title: 'Latest contacts', content: 'This is the content of card 4.' }
    ];

    public get user() {
        return this.userService.getUserData;
    }
}
