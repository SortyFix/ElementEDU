import { Component } from '@angular/core';
import {ThemeService} from "../theming/theme.service";
import {UserService} from "../user/user.service";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
    constructor(public themeService: ThemeService, public userService: UserService) {
    }

    public get user() {
        return this.userService.getUserData;
    }
}
