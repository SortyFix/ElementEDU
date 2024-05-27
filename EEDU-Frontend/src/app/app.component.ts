import {Component, ViewEncapsulation} from '@angular/core';
import {UserService} from "./user/user.service";
import {LoginRequest} from "./user/login/login-request";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class AppComponent {
    constructor(private auth: UserService) {
        auth.login(new LoginRequest("root", "Development123!", false))
    }
}
