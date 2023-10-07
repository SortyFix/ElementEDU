import {ChangeDetectorRef, Component} from '@angular/core';

@Component({
    selector: 'app-head-bar',
    templateUrl: './head-bar.component.html',
    styleUrls: ['./head-bar.component.scss']
})
export class HeadBarComponent {
    time: string = '';
    today: Date = new Date();
    minutes: string = '';

    constructor(private cdr: ChangeDetectorRef) {
        setInterval(() => {
            this.today = new Date();
            this.time = parseNumber(this.today.getDate()) + "."
                + parseNumber(this.today.getMonth() + 1) + "."
                + parseNumber(this.today.getFullYear());
            /*      // // Append a "0" if the minute counter is smaller than 10, stylistic reasoning.
                  // if(this.today.getMinutes() < 10){
                  //   this.minutes = "0" + this.today.getMinutes();
                  // }
                  // else{
                  //   this.minutes = '' + this.today.getMinutes();
                  // }
                  // this.time = this.today.getHours() + ":" + this.minutes;*/
            this.cdr.detectChanges();
        }, 1000);

        function parseNumber(date: number) {
            if (date < 10) {
                return "0" + date;
            }
            return date;
        }

    }
}
