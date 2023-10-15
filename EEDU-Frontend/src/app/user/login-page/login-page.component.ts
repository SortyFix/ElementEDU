import {Component} from '@angular/core';
import {UserService} from "../user.service";

@Component({
    selector: 'app-login-page',
    templateUrl: './login-page.component.html',
    styleUrls: ['./login-page.component.scss']
})
export class LoginPageComponent {
    constructor(private userService: UserService) { }
    login(){
        const username = ((document.getElementById('username') as HTMLInputElement).value);
        const password = ((document.getElementById('password') as HTMLInputElement).value);

        if(!username || username == "" || !password || password == ""){
            console.log("No valid username given.");
            return;
        }
        else{
            console.log("Attempting login with username: '" + username + "', and password '" + password + "'.");
            return this.userService.requestLogin((username), password);
        }
    }
}
