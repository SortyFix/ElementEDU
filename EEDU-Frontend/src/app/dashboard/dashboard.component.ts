import { Component } from '@angular/core';
import {UserService} from "../user/user.service";
import {AppointmentCardComponent} from "./appointment-card/appointment-card.component";
import {AssignmentCardComponent} from "./assignment-card/assignment-card.component";
import {NewsCardComponent} from "./news-card/news-card.component";
import {ChatCardComponent} from "./chat-card/chat-card.component";
import {MatIcon} from "@angular/material/icon";
import {NgComponentOutlet, NgForOf} from "@angular/common";
import {Router, RouterLink} from "@angular/router";

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    imports: [
        MatIcon,
        NgForOf,
        NgComponentOutlet,
        RouterLink
    ],
    standalone: true,
    styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
    constructor(public userService: UserService, public router: Router) {
    }

    cards = [
        { title: 'Next appointments', component: AppointmentCardComponent, route: 'timetable' },
        { title: 'Homework', component: AssignmentCardComponent, route: 'timetable'},
        { title: 'Latest news', component: NewsCardComponent, route: 'news' },
        { title: 'Latest contacts', component: ChatCardComponent, route: 'chat' }
    ];

    public get user() {
        return this.userService.getUserData;
    }

    public navigateTo(route: string): void {
        this.router.navigate([route]);
    }
}
