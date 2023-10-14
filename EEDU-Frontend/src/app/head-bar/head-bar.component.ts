import {ChangeDetectorRef, Component} from '@angular/core';
import {Router} from "@angular/router";

@Component({
    selector: 'app-head-bar', templateUrl: './head-bar.component.html', styleUrls: ['./head-bar.component.scss']
})
export class HeadBarComponent {
    time: string = '';

    constructor(private cdr: ChangeDetectorRef, private router: Router) {
        this.update(); // Ensure date is shown as fast as possible
        setInterval(() => {
            this.update();
            this.cdr.detectChanges();
        }, 1000);
    }

    private update() {
        let today = new Date();
        // Ensure date update
        this.time = today.getDate().toString().padStart(2, '0') + "." + (today.getMonth() + 1).toString().padStart(2, '0') + "." + today.getFullYear();
    }

    public switchPage() {
        this.router.navigate(["/user-login"]).then(() => console.log('Successfully switched page.'));
    }
}
