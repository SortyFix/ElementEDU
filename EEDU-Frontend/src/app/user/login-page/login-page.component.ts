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
            alert("Bitte fülle alle Felder aus.");
            return;
        }
        else{
            console.log("Attempting login with username: '" + username + "', and password '" + password + "'.");
            this.userService.requestLogin(username, password).subscribe(
                error => {
                    if(error == 401){
                        console.log("Der eigegebenen Daten sind nicht korrekt. Bitte versuche es noch einmal.");
                    }
                }
            )
        }
    }
}
