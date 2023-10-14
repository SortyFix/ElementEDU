import {ChangeDetectorRef, Component} from '@angular/core';
import {Router} from "@angular/router";

@Component({
    selector: 'app-head-bar',
    templateUrl: './head-bar.component.html',
    styleUrls: ['./head-bar.component.scss']
})
export class HeadBarComponent {
    time: string = '';
    today: Date = new Date();

    constructor(private cdr: ChangeDetectorRef, private router: Router) {
        setInterval(() => {
            // Show date on header
            this.today = new Date();
            this.time = parseNumber(this.today.getDate()) + "."
                + parseNumber(this.today.getMonth() + 1) + "."
                + parseNumber(this.today.getFullYear());
            // Ensure date update
            this.cdr.detectChanges();
        }, 1000);

        function parseNumber(date: number) {
            if (date < 10) {
                return "0" + date;
            }
            return date;
        }
    }
    switchPage(){
        this.router.navigate(["/user-login"]).then(() => console.log('Successfully switched page.'));
    }
}
