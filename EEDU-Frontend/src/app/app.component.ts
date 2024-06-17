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
    backendReached: boolean = false;

    constructor(public userService: UserService)
    {
    }

    ngOnInit(): void
    {
        this.backendReached = false;
        this.loadUserData();
    }

    loadUserData()
    {
        // load user data when site loads
        this.userService.loadData().subscribe({
            error: error =>
            {
                if (error.status == 403) // not logged in
                {
                    return;
                }
                this.errorSignal.set(this.getErrorMessage(error))
            }
        }).add(() => this.backendReached = !this.errorSignal());
    }

    private getErrorMessage(error: any): string
    {
        const status = error.status;
        switch (status)
        {
            case 0:
                return "Sorry, our servers are having issues right now. Please try again later. We're working to resolve this as soon as possible. Thank you for your patience!";
            default:
                return "An unknown error was encountered. We are sorry for any inconveniences.";
        }
    }
}
