import {Component, inject} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {DashboardComponent} from "../card/dashboard/dashboard.component";
import {map} from "rxjs/operators";

@Component({
    selector: 'app-settings',
    templateUrl: './settings.component.html',
    styleUrls: ['./settings.component.scss'],
    animations: [
        trigger('Fade', [
            state('false', style({opacity: 0})),
            state('true', style({opacity: 1})),
            transition(':enter', animate('150ms'))
        ])
    ]
})
export class SettingsComponent {
    constructor(private dashboard: DashboardComponent) { }
    onClose(){
        this.dashboard.showSettings = false;
    }
    private breakpointObserver = inject(BreakpointObserver);
    settingsLayout = this.breakpointObserver.observe([Breakpoints.Small, Breakpoints.XSmall]).pipe(map(({matches}) => {
        if (matches) {
            return{
                fontSize: "font-size: 30px;",
                xSize: "30",
            }
        }
        return{
            fontSize: "font-size: 50px;",
            xSize: "50"
        }
    }));
}

