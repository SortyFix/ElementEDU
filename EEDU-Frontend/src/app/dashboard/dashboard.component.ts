import { Component } from '@angular/core';
import {UserService} from "../user/user.service";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
    constructor(public userService: UserService) {
    }
}
