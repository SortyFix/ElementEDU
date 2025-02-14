import { Component } from '@angular/core';
import {ThemeService} from "../theming/theme.service";
import {UserService} from "../user/user.service";
import {AppointmentCardComponent} from "./appointment-card/appointment-card.component";
import {AssignmentCardComponent} from "./assignment-card/assignment-card.component";
import {NewsCardComponent} from "./news-card/news-card.component";
import {ChatCardComponent} from "./chat-card/chat-card.component";
import {MatIcon} from "@angular/material/icon";
import {NgComponentOutlet, NgForOf} from "@angular/common";

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    imports: [
        MatIcon,
        NgForOf,
        NgComponentOutlet
    ],
    standalone: true,
    styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
    constructor(public themeService: ThemeService, public userService: UserService) {
    }

    cards = [
        { title: 'Next appointments', content: 'This is the content of card 1.', component: AppointmentCardComponent },
        { title: 'Homework', content: 'This is the content of card 2.', component: AssignmentCardComponent },
        { title: 'Latest news', content: 'This is the content of card 3.', component: NewsCardComponent },
        { title: 'Latest contacts', content: 'This is the content of card 4.', component: ChatCardComponent }
    ];

    public get user() {
        return this.userService.getUserData;
    }
}
