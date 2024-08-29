import {Component, OnInit, signal, ViewEncapsulation, WritableSignal} from '@angular/core';
import {UserService} from "./user/user.service";
import {Router} from "@angular/router";
import {ThemeService} from "./theming/theme.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class AppComponent implements OnInit
{

    errorSignal: WritableSignal<string> = signal('')

    constructor(public userService: UserService, public themeService: ThemeService, public router: Router)
    {
    }

    ngOnInit(): void
    {
        this.loadUserData();
        this.storeUserTheme();
    }

    protected isLoaded(): boolean
    {
        return this.userService.hasLoaded && !this.errorSignal();
    }

    protected loadUserData(): void
    {
        // load user data when site loads
        this.userService.loadData().subscribe({
            error: error =>
            {
                console.log("Error while fetching theme data.", error);
                if (error.status == 403) // not logged in
                {
                    return;
                }
                this.errorSignal.set(this.getErrorMessage(error))
            }
        });
    }

    // Stores the newly fetched theme inside local storage.
    protected storeUserTheme() {
        this.themeService.fetchTheme().subscribe((theme: any) =>
        localStorage.setItem('userTheme', JSON.stringify(theme)));
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
