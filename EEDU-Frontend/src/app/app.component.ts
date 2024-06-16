import {Component, OnInit, signal, ViewEncapsulation, WritableSignal} from '@angular/core';
import {UserService} from "./user/user.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class AppComponent implements OnInit
{

    errorSignal: WritableSignal<string> = signal('')
    load: boolean = false;

    constructor(public userService: UserService)
    {
    }

    ngOnInit(): void
    {
        this.userService.isLoggedIn().subscribe({
            next: value => this.load = true, error: error => this.errorSignal.set(this.getErrorMessage(error))
        }).add(() =>
        {
            console.log("Finish")
        })
    }

    private getErrorMessage(error: any): string
    {
        const status = error.status;
        switch (status)
        {
            case 0:
                return "The service was not able to reach the backend. Are the servers down?"
            default:
                return "An unknown error was encountered. We are sorry for any inconveniences."
        }
    }
}
