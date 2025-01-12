import { Component } from '@angular/core';
import {ThemeService} from "../theming/theme.service";
import {UserService} from "../user/user.service";
import {CdkDragDrop} from "@angular/cdk/drag-drop";
import {AppointmentCardComponent} from "./appointment-card/appointment-card.component";
import {HomeworkCardComponent} from "./homework-card/homework-card.component";
import {NewsComponent} from "../news/news.component";
import {NewsCardComponent} from "./news-card/news-card.component";
import {ChatCardComponent} from "./chat-card/chat-card.component";
import {RGBAColor} from "angularx-qrcode";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
    constructor(public themeService: ThemeService, public userService: UserService) {
    }

    cards = [
        { title: 'Next appointments', content: 'This is the content of card 1.', component: AppointmentCardComponent },
        { title: 'Homework', content: 'This is the content of card 2.', component: HomeworkCardComponent },
        { title: 'Latest news', content: 'This is the content of card 3.', component: NewsCardComponent },
        { title: 'Latest contacts', content: 'This is the content of card 4.', component: ChatCardComponent }
    ];

    public get user() {
        return this.userService.getUserData;
    }
}
